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
  private static final String MODULE_ROOT = "file://$MODULE_DIR$";

  static String makeImlContent(
    String productionOutputDirRelativeToContentRoot,
    String testOutputDirRelativeToContentRoot,
    String generatedSourcesDirRelativeToProductionOutputDir,
    String generatedTestSourcesDirRelativeToTestOutputDir,
    List<String> sourcesRoots,
    List<String> testSourcesRoots,
    List<String> resourcesRoots,
    List<ModuleDependencyEntry> moduleEntries,
    List<JarDependencyEntry> libraryEntries) {

    checkIsNotBlank(productionOutputDirRelativeToContentRoot,
      "productionOutputDirRelativeToContentRoot");
    checkIsNotBlank(testOutputDirRelativeToContentRoot,
      "testOutputDirRelativeToContentRoot");
    checkIsNotBlank(generatedSourcesDirRelativeToProductionOutputDir,
      "generatedSourcesDirRelativeToProductionOutputDir");
    checkIsNotBlank(generatedTestSourcesDirRelativeToTestOutputDir,
      "generatedTestSourcesDirRelativeToTestOutputDir");

    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<module type=\"JAVA_MODULE\" version=\"4\">");

    // inherit-compiler-output MUST BE FALSE (or not present),
    // or many settings in the iml will be superseded
    // by intellij "project-level" settings and defaults.
    lines.add("  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"false\">");
    lines.add("    <exclude-output />");
    lines.add(format("    <output url=\"%s\"/>",
      pathRelativeToModuleRoot(productionOutputDirRelativeToContentRoot)));
    lines.add(format("    <output-test url=\"%s\"/>",
      pathRelativeToModuleRoot(testOutputDirRelativeToContentRoot)));

    lines.add(format("    <content url=\"%s\">", MODULE_ROOT));

    lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"false\" generated=\"true\" />",
      pathRelativeToModuleRoot(
        fileJoin(productionOutputDirRelativeToContentRoot, generatedSourcesDirRelativeToProductionOutputDir))));

    lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"true\" generated=\"true\" />",
      pathRelativeToModuleRoot(
        fileJoin(testOutputDirRelativeToContentRoot, generatedTestSourcesDirRelativeToTestOutputDir))));

    sourcesRoots.forEach(sourcesRoot ->
      lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"false\" />",
        pathRelativeToModuleRoot(sourcesRoot))));

    testSourcesRoots.forEach(testSourcesRoot ->
      lines.add(format("      <sourceFolder url=\"%s\" isTestSource=\"true\" />",
        pathRelativeToModuleRoot(testSourcesRoot))));

    resourcesRoots.forEach(resourcesRoot ->
      lines.add(
        format("      <sourceFolder url=\"%s\" type=\"java-resource\" />",
          pathRelativeToModuleRoot(resourcesRoot))));

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
    return format(MODULE_ROOT + "/%s", pathFromModuleDirToContentRoot.replaceAll("/$", ""));
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
