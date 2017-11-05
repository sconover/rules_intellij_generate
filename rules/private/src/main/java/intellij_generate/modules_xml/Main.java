package intellij_generate.modules_xml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

import static intellij_generate.common.Util.fileJoin;
import static intellij_generate.common.Util.writeStringToFileAsUTF8;
import static intellij_generate.modules_xml.ModulesXmlContent.makeModulesXmlContent;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class Main {
  @Parameter(
    names = {"--iml-path"},
    description = "Paths to intellij iml files, relative to the project root, that should be included in modules.xml.")
  private List<String> imlPathsRelativeToProjectDir = new ArrayList<>();

  @Parameter(
    names = {"--modules-xml-path"},
    description = "The goal of this operation is to generate a modules.xml file whose path is indicated by this parameter.")
  private String modulesXmlPath = null;

  public static void main(String[] args) {
    Main main = new Main();
    JCommander.newBuilder()
      .addObject(main)
      .build()
      .parse(args);
    main.run();
  }

  private void run() {
    writeStringToFileAsUTF8(
      modulesXmlPath,
      makeModulesXmlContent(
        this.imlPathsRelativeToProjectDir.stream()
          .map(imlPath -> fileJoin("$PROJECT_DIR$", imlPath))
          .collect(toList())));
  }

  @Override
  public String toString() {
    return "Make an intellij modules.xml file:\n" +
      format("modulesXmlPath=%s", modulesXmlPath);
  }
}
