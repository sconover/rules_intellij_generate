package intellij_generate.compiler_xml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static intellij_generate.common.Util.checkState;
import static intellij_generate.common.Util.writeStringToFileAsUTF8;
import static intellij_generate.compiler_xml.CompilerXmlContent.makeCompilerXmlContent;
import static java.lang.String.format;

public class Main {
  @Parameter(
    names = {"--generated-sources-subdir"},
    description = "Subdirectory where generated sources from annotation processors will go.")
  private String generatedSourcesSubdir;

  @Parameter(
    names = {"--generated-test-sources-subdir"},
    description = "Subdirectory where generated test sources from annotation processors will go.")
  private String generatedTestSourcesSubdir;

  @Parameter(
    names = {"--module-to-profile-mapping"},
    description = "A mapping of module name to profile name (k=v).")
  private List<String> moduleNameToProfileNameMappings = new ArrayList<>();

  @Parameter(
    names = {"--module-to-annotation-processor-mapping"},
    description = "A mapping of module name to annotation processor name (k=v).")
  private List<String> moduleNameToAnnotationProcessorNameMappings = new ArrayList<>();

  @Parameter(
    names = {"--compiler-xml-path"},
    description = "The goal of this operation is to generate an compiler.xml file whose path is indicated by this parameter.")
  private String compilerXmlPath = null;

  public static void main(String[] args) {
    Main main = new Main();
    JCommander.newBuilder()
      .addObject(main)
      .build()
      .parse(args);
    main.run();
  }

  private void run() {
    Map<String, String> moduleNameToProfileName = new LinkedHashMap<>(); // retains order
    moduleNameToProfileNameMappings.forEach(mapping -> {
      String[] parts = mapping.split("=", 2);
      String moduleName = parts[0];
      String profileName = parts[1];

      checkState(!moduleNameToProfileName.containsKey(moduleName),
        format("module name unexpectedly already had a profile name mapping: " +
          "module name: '%s' profile name: '%s'", moduleName, profileName));
      moduleNameToProfileName.put(moduleName, profileName);
    });

    Map<String, List<String>> moduleNameToAnnotationProcessors = new LinkedHashMap<>(); // retains order
    moduleNameToAnnotationProcessorNameMappings.forEach(mapping -> {
      String[] parts = mapping.split("=", 2);
      String moduleName = parts[0];
      String annotationProcessor = parts[1];

      if (!moduleNameToAnnotationProcessors.containsKey(moduleName)) {
        moduleNameToAnnotationProcessors.put(moduleName, new ArrayList<>());
      }
      List<String> annotationProcessors = moduleNameToAnnotationProcessors.get(moduleName);
      if (!annotationProcessors.contains(moduleName)) {
        annotationProcessors.add(annotationProcessor);
      }
    });

    writeStringToFileAsUTF8(
      compilerXmlPath,
      makeCompilerXmlContent(
        generatedSourcesSubdir,
        generatedTestSourcesSubdir,
        moduleNameToProfileName,
        moduleNameToAnnotationProcessors));
  }


  @Override
  public String toString() {
    return "Make an intellij compiler.xml file:\n" +
      format("compilerXmlPath=%s", compilerXmlPath);
  }
}
