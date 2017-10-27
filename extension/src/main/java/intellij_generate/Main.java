package intellij_generate;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static intellij_generate.Util.fileJoin;
import static intellij_generate.Util.readFile;
import static intellij_generate.Util.writeLinesToFileAsUTF8;
import static java.lang.String.format;
import static java.util.Arrays.asList;
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
    names = {"--test-sources-root", "-tr"},
    description = "Directories under the content root that should be marked as test roots, " +
      "coloring the folder green, and making Intellij treat appropriately-named files under that root as code, " +
      "that is indexed and navigable and so on.")
  private List<String> testSourcesRoots = new ArrayList<>();

  @Parameter(
    names = {"--main-libraries-manifest-path", "-mp"},
    description = "A file in two columns: column 1 is the library label, column 2 is the path to the library, " +
      "of 'main' libraries that the source root files in the project depend upon.")
  private String mainLibrariesManifestPath = null;

  @Parameter(
    names = {"--test-libraries-manifest-path", "-tp"},
    description = "A file in two columns: column 1 is the library label, column 2 is the path to the library, " +
      "of 'test' libraries that the test source root files in the project depend upon " +
      "(but the main source files do not depend on).")
  private String testLibrariesManifestPath = null;

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
    List<LibraryEntry> mainLibraryEntries = loadLibraryEntriesFromManifestFile(mainLibrariesManifestPath);
    List<LibraryEntry> testLibraryEntries = loadLibraryEntriesFromManifestFile(testLibrariesManifestPath);

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

    testSourcesRoots.forEach(testSourcesRoot ->
      lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"true\" />",
        "file://" + fileJoin(pathFromModuleDirToContentRootWithIntellijVariable, testSourcesRoot))));

    lines.add("    </content>");

    lines.add("    <orderEntry type=\"jdk\" jdkName=\"1.8\" jdkType=\"JavaSDK\" />");
    lines.add("    <orderEntry type=\"sourceFolder\" forTests=\"false\" />");

    if (!mainLibraryEntries.isEmpty()) {
      mainLibraryEntries.forEach(
        libraryEntry ->
          addLibraryOrderEntryLines(
            pathFromModuleDirToContentRootWithIntellijVariable,
            lines,
            libraryEntry));
    }

    if (!testLibraryEntries.isEmpty()) {
      testLibraryEntries.forEach(
        libraryEntry ->
          addLibraryOrderEntryLines(
            pathFromModuleDirToContentRootWithIntellijVariable,
            lines,
            libraryEntry,
            " scope=\"TEST\""));
    }

    lines.add("  </component>");
    lines.add("</module>");

    writeLinesToFileAsUTF8(imlPath, lines);
  }

  private static void addLibraryOrderEntryLines(
    String pathFromModuleDirToContentRootWithIntellijVariable,
    List<String> lines,
    LibraryEntry libraryEntry) {
    addLibraryOrderEntryLines(
      pathFromModuleDirToContentRootWithIntellijVariable,
      lines,
      libraryEntry,
      "");
  }

  private static void addLibraryOrderEntryLines(
    String pathFromModuleDirToContentRootWithIntellijVariable,
    List<String> lines,
    LibraryEntry libraryEntry,
    String extraOrderEntryAttributes) {
    String libraryPath = "jar://" + fileJoin(pathFromModuleDirToContentRootWithIntellijVariable, libraryEntry.path);

    lines.add(format("    <orderEntry type=\"module-library\"%s>", extraOrderEntryAttributes));
    lines.add("      <library>");
    lines.add("        <CLASSES>");
    lines.add(format("          <root url=\"%s!/\" />", libraryPath));
    lines.add("        </CLASSES>");
    lines.add("        <JAVADOC />");
    lines.add("        <SOURCES />");
    lines.add("      </library>");
    lines.add("    </orderEntry>");
  }

  private List<LibraryEntry> loadLibraryEntriesFromManifestFile(String librariesManifestPath) {
    return asList(readFile(librariesManifestPath).split("\n")).stream()
      .map(line -> {
        String[] parts = line.split(" ");
        String name = parts[0];
        String path = parts[1];
        return new LibraryEntry(name, path);
      })
      .collect(toList());
  }

  @Override
  public String toString() {
    return "Make an intellij iml file:\n" +
      format("contentRoot=%s", contentRoot) + "\n" +
      format("sourcesRoots=%s", sourcesRoots) + "\n" +
      format("imlPath=%s", imlPath);
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
}
