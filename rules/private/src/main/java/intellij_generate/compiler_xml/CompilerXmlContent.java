package intellij_generate.compiler_xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static intellij_generate.common.Util.checkState;
import static java.lang.String.format;

public class CompilerXmlContent {
  public static String makeCompilerXmlContent(
    String generatedSourcesSubdir,
    String generatedTestSourcesSubdir,
    Map<String, String> moduleNameToProfileName,
    Map<String, List<String>> moduleNameToAnnotationProcessors) {

    // transform the iml-module-centric inputs to
    // intellij annotation processor profile-centric structures
    Map<String, List<String>> profileNameToModuleNames = new LinkedHashMap<>();
    Map<String, List<String>> profileNameToAnnotationProcessors = new LinkedHashMap<>();
    moduleNameToProfileName.forEach((moduleName, profileName) -> {
      if (!profileNameToModuleNames.containsKey(profileName)) {
        profileNameToModuleNames.put(profileName, new ArrayList<>());
      }
      profileNameToModuleNames.get(profileName).add(moduleName);

      if (!profileNameToAnnotationProcessors.containsKey(profileName)) {
        profileNameToAnnotationProcessors.put(profileName, new ArrayList<>());
      }
      List<String> annotationProcessorsForModule = moduleNameToAnnotationProcessors.get(moduleName);
      checkState(annotationProcessorsForModule!=null,
        format("Unexpectedly did not find annotation processors" +
        " for module '%s'", moduleName));
      List<String> annotationProcessorsForProfile = profileNameToAnnotationProcessors.get(profileName);
      annotationProcessorsForModule.forEach(annotationProcessor -> {
        if (!annotationProcessorsForProfile.contains(annotationProcessor)) {
          annotationProcessorsForProfile.add(annotationProcessor);
        }
      });
    });


    // using the profile structures, create compiler.xml content.

    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<project version=\"4\">");
    lines.add("  <component name=\"CompilerConfiguration\">");
    lines.add("    <annotationProcessing>");

    profileNameToModuleNames.forEach((profileName, moduleNames) -> {
      lines.add(format("      <profile name=\"%s\" enabled=\"true\">", profileName));
      lines.add(format("        <sourceOutputDir name=\"%s\" />", generatedSourcesSubdir));
      lines.add(format("        <sourceTestOutputDir name=\"%s\" />", generatedTestSourcesSubdir));

      profileNameToAnnotationProcessors.get(profileName).forEach(annotationProcessor ->
        lines.add(format("        <processor name=\"%s\" />", annotationProcessor)));

      moduleNames.forEach(moduleName -> lines.add(format("        <module name=\"%s\" />", moduleName)));

      lines.add("      </profile>");
    });

    lines.add("    </annotationProcessing>");
    lines.add("  </component>");
    lines.add("</project>");

    return lines.stream().collect(Collectors.joining("\n"));
  }
}
