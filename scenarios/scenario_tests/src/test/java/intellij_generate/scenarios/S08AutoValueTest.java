package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S08AutoValueTest {
  private static String compilerXmlContent;

  @BeforeAll
  public static void before_all() {
    compilerXmlContent = loadBazelGeneratedFile("08_auto_value/compiler_xml_compiler.xml");
  }

  @Test
  public void annotation_processor_config() {
    assertEquals(
      asList("my_idea_auto_value_annotation_processor_profile"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile/@name"));

    assertEquals(
      asList("08_auto_value"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='my_idea_auto_value_annotation_processor_profile']/module/@name"));

    assertEquals(
      asList(
        "com.google.auto.value.processor.AutoValueProcessor"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='my_idea_auto_value_annotation_processor_profile']/processor/@name"));
  }
}
