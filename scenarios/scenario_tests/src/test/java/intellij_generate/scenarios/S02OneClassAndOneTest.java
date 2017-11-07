package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpath;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S02OneClassAndOneTest {
  private static String imlContent;

  @BeforeAll
  public static void before_all() {
    imlContent = loadBazelGeneratedFile("02_one_class_and_one_test/idea_root_module.iml");
  }

  @Test
  public void source_folders() {
    // this scenario has one source file, in a non-maven-standard layout
    assertEquals(asList(
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./out/production/generated",
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./out/test/generated_tests",
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./src",
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./test",
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./src/main/resources",
      "file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./src/test/resources"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false", "true", "false", "true"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  @Test
  public void output_folders() {
    assertEquals("file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./out/production",
      xpath(imlContent, "/module/component/output/@url"));

    assertEquals("file://$MODULE_DIR$/../../../../02_one_class_and_one_test/./out/test",
      xpath(imlContent, "/module/component/output-test/@url"));
  }

  @Test
  public void libraries() {
    // has no "main" dependencies
    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

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
        xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url")));
  }

}
