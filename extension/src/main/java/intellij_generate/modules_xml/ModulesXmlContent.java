package intellij_generate.modules_xml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

class ModulesXmlContent {
  static String makeModulesXmlContent(List<String> imlPaths) {
    List<String> lines = new ArrayList<>();
    lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    lines.add("<project version=\"4\">");
    lines.add("  <component name=\"ProjectModuleManager\">");
    lines.add("    <modules>");
    imlPaths.forEach(imlPath -> {
      lines.add(format("      <module fileurl=\"file://%s\" filepath=\"%s\" />", imlPath, imlPath));
    });
    lines.add("    </modules>");
    lines.add("  </component>");
    lines.add("</project>");

    return lines.stream().collect(Collectors.joining("\n"));
  }
}
