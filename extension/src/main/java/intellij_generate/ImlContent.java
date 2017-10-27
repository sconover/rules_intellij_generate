package intellij_generate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static intellij_generate.Util.fileJoin;
import static java.lang.String.format;

public class ImlContent {
  static String makeImlContent(
    String pathFromModuleDirToContentRoot,
    List<String> sourcesRoots,
    List<String> testSourcesRoots,
    List<JarLibraryEntry> mainLibraryEntries,
    List<JarLibraryEntry> testLibraryEntries) {

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
        jarLibraryEntry ->
          addLibraryOrderEntryLines(
            lines,
            jarLibraryEntry));
    }

    if (!testLibraryEntries.isEmpty()) {
      testLibraryEntries.forEach(
        jarLibraryEntry ->
          addLibraryOrderEntryLines(
            lines,
            jarLibraryEntry,
            " scope=\"TEST\""));
    }

    lines.add("  </component>");
    lines.add("</module>");

    return lines.stream().collect(Collectors.joining("\n"));
  }

  private static void addLibraryOrderEntryLines(List<String> lines, JarLibraryEntry jarLibraryEntry) {
    addLibraryOrderEntryLines(
      lines,
      jarLibraryEntry,
      "");
  }

  private static void addLibraryOrderEntryLines(
    List<String> lines,
    JarLibraryEntry jarLibraryEntry,
    String extraOrderEntryAttributes) {
    String libraryPath = "jar://" + jarLibraryEntry.path;

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
}
