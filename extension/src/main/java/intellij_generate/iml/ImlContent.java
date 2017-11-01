package intellij_generate.iml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static intellij_generate.common.Util.fileJoin;
import static intellij_generate.iml.JarDependencyEntry.Scope.COMPILE;
import static intellij_generate.iml.JarDependencyEntry.Scope.TEST;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

class ImlContent {
  static String makeImlContent(
    String pathFromModuleDirToContentRoot,
    List<String> sourcesRoots,
    List<String> testSourcesRoots,
    List<ModuleDependencyEntry> moduleEntries,
    List<JarDependencyEntry> libraryEntries) {
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

    moduleEntries.forEach(moduleDependencyEntry -> {
      lines.add(format("    <orderEntry type=\"module\" module-name=\"%s\" />", moduleDependencyEntry.name));
    });

    List<JarDependencyEntry> compileLibraryEntries = libraryEntries.stream()
      .filter(l -> l.scope.equals(COMPILE))
      .collect(toList());
    List<JarDependencyEntry> testLibraryEntries = libraryEntries.stream()
      .filter(l -> l.scope.equals(TEST))
      .collect(toList());

    if (!compileLibraryEntries.isEmpty()) {
      compileLibraryEntries.forEach(
        jarDependencyEntry ->
          addJarOrderEntryLines(
            lines,
            jarDependencyEntry));
    }

    // entries already accounted for in the main lib list, should be removed from test libs.
    testLibraryEntries = new ArrayList<>(testLibraryEntries);
    testLibraryEntries.removeAll(compileLibraryEntries);

    if (!testLibraryEntries.isEmpty()) {
      testLibraryEntries.forEach(
        jarDependencyEntry ->
          addJarOrderEntryLines(
            lines,
            jarDependencyEntry,
            " scope=\"TEST\""));
    }

    lines.add("  </component>");
    lines.add("</module>");

    return lines.stream().collect(Collectors.joining("\n"));
  }

  private static void addJarOrderEntryLines(List<String> lines, JarDependencyEntry jarDependencyEntry) {
    addJarOrderEntryLines(
      lines,
      jarDependencyEntry,
      "");
  }

  private static void addJarOrderEntryLines(
    List<String> lines,
    JarDependencyEntry jarDependencyEntry,
    String extraOrderEntryAttributes) {
    String libraryPath = "jar://" + jarDependencyEntry.path;

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
