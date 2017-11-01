package intellij_generate.modules_xml;

import org.junit.jupiter.api.Test;

import static intellij_generate.modules_xml.ModulesXmlContent.makeModulesXmlContent;
import static intellij_generate.testcommon.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModulesXmlContentTest {
  @Test
  public void references_provided_iml_files() {
    String modulesXmlContent =
      makeModulesXmlContent(asList("/module/a.iml", "/module/b.iml"));

    assertEquals(asList(
      "file:///module/a.iml",
      "file:///module/b.iml"),
      xpathList(modulesXmlContent, "/project/component/modules/module/@fileurl"));

    assertEquals(asList(
      "/module/a.iml",
      "/module/b.iml"),
      xpathList(modulesXmlContent, "/project/component/modules/module/@filepath"));
  }
}
