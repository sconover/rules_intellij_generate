package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.xpath;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S01OneClassTest {
  private static String imlContent;

  @BeforeAll
  public static void before_all() {
    imlContent = loadBazelGeneratedFile("01_one_class/idea_root_module.iml");
  }

  @Test
  public void source_folders() {
    // this scenario has one source file, in a non-maven-standard layout
    assertEquals(asList(
      "file://$MODULE_DIR$/../../../../01_one_class/./out/production/generated",
      "file://$MODULE_DIR$/../../../../01_one_class/./out/test/generated_tests",
      "file://$MODULE_DIR$/../../../../01_one_class/./src"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false", "true", "false"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  @Test
  public void output_folders() {
    assertEquals("file://$MODULE_DIR$/../../../../01_one_class/./out/production",
      xpath(imlContent, "/module/component/output/@url"));

    assertEquals("file://$MODULE_DIR$/../../../../01_one_class/./out/test",
      xpath(imlContent, "/module/component/output-test/@url"));
  }

  @Test
  public void libraries() {
    // this scenario has no jar dependencies.
    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library']/library/CLASSES/root/@url"));
  }
}
