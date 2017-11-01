package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedImlFile;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S01OneClassTest {
  private static String imlContent;

  @BeforeAll
  public static void before_all() {
    imlContent = loadBazelGeneratedImlFile("01_one_class/idea_root_module.iml");
  }

  @Test
  public void source_folders() {
    // this scenario has one source file, in a non-maven-standard layout
    assertEquals(asList(
      "file://$MODULE_DIR$/../../../../01_one_class/./src"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  @Test
  public void libraries() {
    // this scenario has no jar dependencies.
    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library']/library/CLASSES/root/@url"));
  }
}
