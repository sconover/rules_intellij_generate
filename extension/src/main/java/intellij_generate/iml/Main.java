package intellij_generate.iml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static intellij_generate.common.Util.checkPathExists;
import static intellij_generate.common.Util.getExecRootPathInAnExtremelyEvilWayDoNotReleaseBeforeCheckingWithBazelTeam;
import static intellij_generate.common.Util.writeStringToFileAsUTF8;
import static intellij_generate.iml.ImlContent.makeImlContent;
import static intellij_generate.iml.JarLibraryEntry.loadLibraryEntriesFromManifestFile;
import static java.lang.String.format;

public class Main {
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

  private void run() {
    String execRootPath = getExecRootPathInAnExtremelyEvilWayDoNotReleaseBeforeCheckingWithBazelTeam();

    Path pathOfImlDir = checkPathExists(Paths.get(new File(imlPath).getParent()).toAbsolutePath());
    Path pathOfContentRoot = checkPathExists(Paths.get(contentRoot).toAbsolutePath());
    Path pathFromModuleDirToContentRoot = pathOfImlDir.relativize(pathOfContentRoot);

    String imlContent =
      makeImlContent(
        pathFromModuleDirToContentRoot.toString(),
        sourcesRoots,
        testSourcesRoots,
        loadLibraryEntriesFromManifestFile(execRootPath, mainLibrariesManifestPath),
        loadLibraryEntriesFromManifestFile(execRootPath, testLibrariesManifestPath));

    writeStringToFileAsUTF8(imlPath, imlContent);
  }

  @Override
  public String toString() {
    return "Make an intellij iml file:\n" +
      format("contentRoot=%s", contentRoot) + "\n" +
      format("sourcesRoots=%s", sourcesRoots) + "\n" +
      format("imlPath=%s", imlPath);
  }
}
