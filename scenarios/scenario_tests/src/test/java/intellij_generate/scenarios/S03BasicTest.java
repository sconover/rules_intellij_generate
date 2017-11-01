package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedImlFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S03BasicTest {
  private static String dolphinImlContent;
  private static String humanImlContent;
  private static String gorillaImlContent;
  private static String primateImlContent;
  private static String mammalImlContent;

  @BeforeAll
  public static void before_all() {
    dolphinImlContent = loadBazelGeneratedImlFile("03_basic/dolphin/idea_dolphin_module.iml");
    humanImlContent = loadBazelGeneratedImlFile("03_basic/human/idea_human_module.iml");
    gorillaImlContent = loadBazelGeneratedImlFile("03_basic/gorilla/idea_gorilla_module.iml");
    primateImlContent = loadBazelGeneratedImlFile("03_basic/primate/idea_primate_module.iml");
    mammalImlContent = loadBazelGeneratedImlFile("03_basic/mammal/idea_mammal_module.iml");
  }

  @Test
  public void source_folders_sample() {
    // this scenario has one source file, in a non-maven-standard layout
    assertEquals(asList(
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/main/java",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/test/java"),
      xpathList(dolphinImlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false", "true"),
      xpathList(dolphinImlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  @Test
  public void sample_jar_dependencies() {
    // has no "main" dependencies
    assertEquals(emptyList(),
      xpathList(dolphinImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    // has standard junit 5 test jar dependencies
    assertEquals(asList(
      "external/org_junit_jupiter_junit_jupiter_api/jar/junit-jupiter-api-5.0.1.jar!/",
      "external/org_junit_jupiter_junit_jupiter_engine/jar/junit-jupiter-engine-5.0.1.jar!/",
      "external/org_junit_platform_junit_platform_commons/jar/junit-platform-commons-1.0.1.jar!/",
      "external/org_junit_platform_junit_platform_console/jar/junit-platform-console-1.0.1.jar!/",
      "external/org_junit_platform_junit_platform_engine/jar/junit-platform-engine-1.0.1.jar!/",
      "external/org_junit_platform_junit_platform_launcher/jar/junit-platform-launcher-1.0.1.jar!/",
      "external/org_junit_platform_junit_platform_runner/jar/junit-platform-runner-1.0.1.jar!/",
      "external/org_opentest4j_opentest4j/jar/opentest4j-1.0.0.jar!/"),
      removeWorkingDirectory(
        xpathList(dolphinImlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url")));
  }

  @Test
  public void module_dependencies() {
    assertEquals(emptyList(),
      xpathList(mammalImlContent, "/module/component/orderEntry[@type='module']/@module-name"));

    assertEquals(asList("idea_mammal_module"),
      xpathList(primateImlContent, "/module/component/orderEntry[@type='module']/@module-name"));

    assertEquals(asList("idea_mammal_module"),
      xpathList(dolphinImlContent, "/module/component/orderEntry[@type='module']/@module-name"));

    assertEquals(asList("idea_mammal_module", "idea_primate_module"),
      xpathList(humanImlContent, "/module/component/orderEntry[@type='module']/@module-name"));

    assertEquals(asList("idea_mammal_module", "idea_primate_module"),
      xpathList(gorillaImlContent, "/module/component/orderEntry[@type='module']/@module-name"));
  }
}
