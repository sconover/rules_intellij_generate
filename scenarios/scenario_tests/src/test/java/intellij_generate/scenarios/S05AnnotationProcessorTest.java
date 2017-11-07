package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S05AnnotationProcessorTest {
  private static String compilerXmlContent;

  @BeforeAll
  public static void before_all() {
    compilerXmlContent = loadBazelGeneratedFile("05_annotation_processor/idea_annotation_processors_compiler.xml");
  }

  @Test
  public void annotation_processor_config() {
    assertEquals(
      asList("foo_profile"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile/@name"));

    assertEquals(
      asList("idea_usage_module"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='foo_profile']/module/@name"));

    assertEquals(
      asList(
        "annotation_processor.class_generator.ClassGeneratorAnnotationProcessor",
        "annotation_processor.text_file_generator.TextFileGeneratorAnnotationProcessor"),
      xpathList(compilerXmlContent, "/project/component/annotationProcessing/profile[@name='foo_profile']/processor/@name"));
  }
}
