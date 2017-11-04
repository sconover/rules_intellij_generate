package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedImlFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S04TransitiveWithExportTest {
  private static String grandparentImlContent;
  private static String parentImlContent;
  private static String childImlContent;

  @BeforeAll
  public static void before_all() {
    grandparentImlContent = loadBazelGeneratedImlFile("04_transitive_via_export/grandparent/idea_grandparent_module.iml");
    parentImlContent = loadBazelGeneratedImlFile("04_transitive_via_export/parent/idea_parent_module.iml");
    childImlContent = loadBazelGeneratedImlFile("04_transitive_via_export/child/idea_child_module.iml");
  }

  @Test
  public void sample_jar_dependencies() {
    assertEquals(asList("external/com_google_guava_guava/jar/guava-19.0.jar!/"),
      removeWorkingDirectory(
        xpathList(grandparentImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url")));

    assertEquals(asList("external/com_google_guava_guava/jar/guava-19.0.jar!/"),
      removeWorkingDirectory(
        xpathList(parentImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url")));

    assertEquals(asList("external/com_google_guava_guava/jar/guava-19.0.jar!/"),
      removeWorkingDirectory(
        xpathList(childImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url")));
  }
}