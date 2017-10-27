package intellij_generate;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class Main {
  public static final String CLASSLOADER_PREFIX_PATH = "CLASSLOADER_PREFIX_PATH";
  @Parameter(
    names = {"--content-root", "-cr"},
    description = "Path, typically relative to the iml's MODULE_DIR. Intellij will " +
      "present files under this directory as the module's apparent contents.")
  private String contentRoot = null;

  @Parameter(
    names = {"--sources-root", "-sr"},
    description = "Directories under the content root that should be marked as source roots, " +
      "coloring the folder blue, and making Intellij treat appropriately-named files under that root as code, " +
      "that is indexed and navigable and so on.")
  private List<String> sourcesRoots = new ArrayList<>();

  @Parameter(
    names = {"--iml-path", "-iml"},
    description = "The goal of this operation is to generate an iml file whose path is indicated by this parameter.")
  private String imlPath = null;

  public static void main(String[] args) {
    Main main = new Main();
    JCommander.newBuilder()
      .addObject(main)
      .build()
      .parse(args);
    main.run();
  }

  public void run() {
    String classloaderPrefixPath = System.getenv(CLASSLOADER_PREFIX_PATH);
    checkState(classloaderPrefixPath != null, "env var CLASSLOADER_PREFIX_PATH must be present");

    // 1) Walk the file tree rooted at CLASSLOADER_PREFIX_PATH
    // 2) Filter down to any subdir/jars that are under "external"...
    //    but exclude local_jdk from this.
    // 3) Finally, remove the CLASSLOADER_PREFIX_PATH from the library file path.
    //    this makes it so intellij can access consistently-available files laid
    //    out in the same structure, but directly under the WORKSPACE root.
    //
    // Some precedent:
    // https://github.com/bazelbuild/intellij/blob/master/aspect/intellij_info_impl.bzl#L48
    //
    // The bazel java_binary wrapper script template:
    // https://github.com/bazelbuild/bazel/blob/master/src/main/java/com/google/devtools/build/lib/bazel/rules/java/java_stub_template.txt
    //
    // Also see the bazel java_stub_template, which is what hands us the CLASSLOADER_PREFIX_PATH
    // env var.
    //
    // The approach used here is likely too brittle, and may be improved by making use of other
    // representations of the same information (e.g. from a MANIFEST file...however the MANIFEST
    // referred to in the above link is not available at runtime).
    //
    // I was also a little unsure about effectively treating the bazel output dirs as a public
    // interface, however the bazelbuild/intellij reference, as well as the existence of documentation
    // about the output dir structure:
    // https://docs.bazel.build/versions/master/output_directories.html
    // convinced me it's not terrible to do.
    List<LibraryEntry> libraryEntries =
      classloaderPrefixPathFileTree(classloaderPrefixPath).stream()
        .filter(f -> f.startsWith(classloaderPrefixPath + "external") &&
          !f.startsWith(classloaderPrefixPath + "external/local_jdk"))
        .map(f -> new LibraryEntry(f.replace(classloaderPrefixPath, ""), f))
        .collect(toList());

    Path pathOfImlDir = Paths.get(new File(imlPath).getParent()).toAbsolutePath();
    Path pathOfContentRoot = Paths.get(contentRoot).toAbsolutePath();
    String pathFromModuleDirToContentRoot = pathOfImlDir.relativize(pathOfContentRoot).toString();
    String pathFromModuleDirToContentRootWithIntellijVariable =
      format("$MODULE_DIR$/%s", pathFromModuleDirToContentRoot.replaceAll("/$", ""));

    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<module type=\"JAVA_MODULE\" version=\"4\">");
    lines.add("  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">");
    lines.add("    <exclude-output />");
    lines.add(format("    <content url=\"%s\">", "file://" + pathFromModuleDirToContentRootWithIntellijVariable));
    sourcesRoots.forEach(sourcesRoot ->
      lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"false\" />",
        "file://" + fileJoin(pathFromModuleDirToContentRootWithIntellijVariable, sourcesRoot))));
    lines.add("    </content>");
    lines.add("    <orderEntry type=\"jdk\" jdkName=\"1.8\" jdkType=\"JavaSDK\" />");
    lines.add("    <orderEntry type=\"sourceFolder\" forTests=\"false\" />");

    if (!libraryEntries.isEmpty()) {
      libraryEntries.forEach(libraryEntry -> {
        String libraryPath = "jar://" + fileJoin(pathFromModuleDirToContentRootWithIntellijVariable, libraryEntry.path);

        lines.add("    <orderEntry type=\"module-library\">");
        lines.add("      <library>");
        lines.add("        <CLASSES>");
        lines.add(format("          <root url=\"%s!/\" />", libraryPath));
        lines.add("        </CLASSES>");
        lines.add("        <JAVADOC />");
        lines.add("        <SOURCES />");
        lines.add("      </library>");
        lines.add("    </orderEntry>");
      });
    }

    lines.add("  </component>");
    lines.add("</module>");

    writeLinesToFileAsUTF8(imlPath, lines);
  }

  @Override
  public String toString() {
    return "Make an intellij iml file:\n" +
      format("contentRoot=%s", contentRoot) + "\n" +
      format("sourcesRoots=%s", sourcesRoots) + "\n" +
      format("imlPath=%s", imlPath);
  }

  private static List<String> classloaderPrefixPathFileTree(String classloaderPrefixPath) {
    try {
      return Files.walk(Paths.get(classloaderPrefixPath))
        .filter(Files::isRegularFile)
        .map(f -> f.toString())
        .collect(toList());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static class LibraryEntry {
    public final String name;
    public final String path;

    public LibraryEntry(String name, String path) {
      this.name = name;
      this.path = path;
    }

    @Override
    public String toString() {
      return format("LibraryEntry[%s,%s]", name, path);
    }
  }

  private static void writeLinesToFileAsUTF8(String path, List<String> lines) {
    new File(new File(path).getParent()).mkdirs();
    try {
      System.out.println(
        format(">> IML CONTENT START %s", path) + "\n" +
          lines.stream().collect(Collectors.joining("\n")) + "\n" +
          format("<< IML CONTENT FINISH %s", path));
      Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String fileJoin(String aPath, String bPath) {
    return new File(aPath, bPath).getPath();
  }

  private static void checkState(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }
}
