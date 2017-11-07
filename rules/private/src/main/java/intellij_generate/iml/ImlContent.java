package intellij_generate.iml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static intellij_generate.common.Util.checkIsNotBlank;
import static intellij_generate.common.Util.fileJoin;
import static intellij_generate.iml.JarDependencyEntry.Scope.COMPILE;
import static intellij_generate.iml.JarDependencyEntry.Scope.TEST;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

class ImlContent {
  static String makeImlContent(
    String pathFromModuleDirToContentRoot,
    String productionOutputDirRelativeToContentRoot,
    String testOutputDirRelativeToContentRoot,
    String generatedSourcesDirRelativeToProductionOutputDir,
    String generatedTestSourcesDirRelativeToTestOutputDir,
    List<String> sourcesRoots,
    List<String> testSourcesRoots,
    List<ModuleDependencyEntry> moduleEntries,
    List<JarDependencyEntry> libraryEntries) {
    checkIsNotBlank(pathFromModuleDirToContentRoot, "pathFromModuleDirToContentRoot");
    checkIsNotBlank(productionOutputDirRelativeToContentRoot, "productionOutputDirRelativeToContentRoot");
    checkIsNotBlank(testOutputDirRelativeToContentRoot, "testOutputDirRelativeToContentRoot");

    String pathFromModuleDirToContentRootWithIntellijVariable = pathRelativeToModuleRoot(pathFromModuleDirToContentRoot);
    String productionOutputDir =
      fileJoin(
        pathFromModuleDirToContentRootWithIntellijVariable,
        productionOutputDirRelativeToContentRoot);
    String testOutputDir =
      fileJoin(
        pathFromModuleDirToContentRootWithIntellijVariable,
        testOutputDirRelativeToContentRoot);

    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<module type=\"JAVA_MODULE\" version=\"4\">");

    // inherit-compiler-output MUST BE FALSE (or not present),
    // or many settings in the iml will be superseded
    // by intellij "project-level" settings and defaults.
    lines.add("  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"false\">");
    lines.add("    <exclude-output />");
    lines.add(format("    <output url=\"file://%s\"/>", productionOutputDir));
    lines.add(format("    <output-test url=\"file://%s\"/>", testOutputDir));

    lines.add(format("    <content url=\"%s\">", "file://" + pathFromModuleDirToContentRootWithIntellijVariable));

    lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"false\" generated=\"true\" />",
      "file://" + fileJoin(productionOutputDir, generatedSourcesDirRelativeToProductionOutputDir)));
    lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"true\" generated=\"true\" />",
      "file://" + fileJoin(testOutputDir, generatedTestSourcesDirRelativeToTestOutputDir)));

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

  private static String pathRelativeToModuleRoot(String pathFromModuleDirToContentRoot) {
    return format("$MODULE_DIR$/%s", pathFromModuleDirToContentRoot.replaceAll("/$", ""));
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
