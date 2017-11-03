package intellij_generate.compiler_xml;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static intellij_generate.compiler_xml.CompilerXmlContent.makeCompilerXmlContent;
import static intellij_generate.testcommon.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompilerXmlContentTest {
  @Test
  public void makes_intellij_annotation_processor_profiles() {
    String compilerXmlContent =
      makeCompilerXmlContent(
        "my_generated_sources",
        "my_generated_test_sources",
        new LinkedHashMap<String, String>() {{
          put("module_a", "profile_one");
          put("module_b", "profile_one");
          put("module_c", "profile_two");
        }},
        new LinkedHashMap<String, List<String>>() {{
          put("module_a", asList("annotation.ProcessorOne", "annotation.ProcessorTwo"));
          put("module_b", asList("annotation.ProcessorOne", "annotation.ProcessorThree"));
          put("module_c", asList("annotation.ProcessorFour"));
        }}
      );

    assertEquals(
      asList("profile_one", "profile_two"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile/@name"));

    assertEquals(
      asList("my_generated_sources", "my_generated_sources"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile/sourceOutputDir/@name"));

    assertEquals(
      asList("my_generated_test_sources", "my_generated_test_sources"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile/sourceTestOutputDir/@name"));

    assertEquals(
      asList("annotation.ProcessorOne", "annotation.ProcessorTwo", "annotation.ProcessorThree"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='profile_one']/processor/@name"));

    assertEquals(
      asList("module_a", "module_b"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='profile_one']/module/@name"));

    assertEquals(
      asList("annotation.ProcessorFour"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='profile_two']/processor/@name"));

    assertEquals(
      asList("module_c"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='profile_two']/module/@name"));
  }
}
