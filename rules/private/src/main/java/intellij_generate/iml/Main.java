package intellij_generate.iml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static intellij_generate.common.Util.getExecRootPathInAnExtremelyEvilWayDoNotReleaseBeforeCheckingWithBazelTeam;
import static intellij_generate.common.Util.writeStringToFileAsUTF8;
import static intellij_generate.iml.ImlContent.makeImlContent;
import static intellij_generate.iml.JarDependencyEntry.loadLibraryEntriesFromManifestFile;
import static intellij_generate.iml.ModuleDependencyEntry.loadModuleEntriesFromManifestFile;
import static java.lang.String.format;

public class Main {
  @Parameter(
    names = {"--sources-root"},
    description = "Directories under the content root that should be marked as source roots, " +
      "coloring the folder blue, and making Intellij treat appropriately-named files under that root as code, " +
      "that is indexed and navigable and so on.")
  private List<String> sourcesRoots = new ArrayList<>();

  @Parameter(
    names = {"--test-sources-root"},
    description = "Directories under the content root that should be marked as test roots, " +
      "coloring the folder green, and making Intellij treat appropriately-named files under that root as code, " +
      "that is indexed and navigable and so on.")
  private List<String> testSourcesRoots = new ArrayList<>();

  @Parameter(
    names = {"--resources-root"},
    description = "Java resource directory roots.")
  private List<String> resourcesRoots = new ArrayList<>();

  @Parameter(
    names = {"--libraries-manifest-path"},
    description = "A file in three columns: " +
      "column 1 is the library label, " +
      "column 2 is the path to the library, " +
      "column 3 is the scope (either COMPILE or TEST), " +
      "of libraries that the source root files in the project depend upon.")
  private String librariesManifestPath = null;

  @Parameter(
    names = {"--modules-manifest-path"},
    description = "A file in two columns: " +
      "column 1 is the module name, " +
      "column 2 is the scope (either COMPILE or TEST), " +
      "of idea modules in the project that this module depends upon.")
  private String modulesManifestPath = null;

  @Parameter(
    names = {"--production-output-dir"},
    description = "Path relative to the content root, where production/main class files, generated " +
      "code, and other output, will be written, by intellij.")
  private String productionOutputDir = null;

  @Parameter(
    names = {"--test-output-dir"},
    description = "Path relative to the content root, where test class files, generated " +
      "code, and other output, will be written, by intellij.")
  private String testOutputDir = null;

  @Parameter(
    names = {"--generated-sources-dir"},
    description = "Path relative to the production output directory, where generated production/main class files" +
      " and resources will be written, by intellij.")
  private String generatedSourcesDir = null;

  @Parameter(
    names = {"--generated-test-sources-dir"},
    description = "Path relative to the test output directory, where generated test class files and resources" +
      " will be written, by intellij.")
  private String generatedTestSourcesDir = null;

  @Parameter(
    names = {"--iml-path"},
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

  private void run() {
    try {
      String execRootPath = getExecRootPathInAnExtremelyEvilWayDoNotReleaseBeforeCheckingWithBazelTeam();

      String imlContent =
        makeImlContent(
          productionOutputDir,
          testOutputDir,
          generatedSourcesDir,
          generatedTestSourcesDir,
          sourcesRoots,
          testSourcesRoots,
          resourcesRoots,
          loadModuleEntriesFromManifestFile(modulesManifestPath),
          loadLibraryEntriesFromManifestFile(execRootPath, librariesManifestPath));

      writeStringToFileAsUTF8(imlPath, imlContent);
    } catch (Exception ex) {
      throw new RuntimeException(format("Exception occurred while processing iml file: %s", this.toString()), ex);
    }
  }

  @Override
  public String toString() {
    return "\nCreate IML:\n" +
      format("  sourcesRoots=%s", sourcesRoots) + "\n" +
      format("  testSourcesRoots=%s", testSourcesRoots) + "\n" +
      format("  modulesManifestPath=%s", modulesManifestPath) + "\n" +
      format("  librariesManifestPath=%s", librariesManifestPath) + "\n" +
      format("  imlPath=%s\n", imlPath);
  }
}
