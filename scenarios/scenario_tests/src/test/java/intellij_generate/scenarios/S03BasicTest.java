package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S03BasicTest {
  private static String dolphinImlContent;
  private static String humanImlContent;
  private static String gorillaImlContent;
  private static String primateImlContent;
  private static String mammalImlContent;

  private static String modulesXmlContent;

  @BeforeAll
  public static void before_all() {
    dolphinImlContent = loadBazelGeneratedFile("03_basic/dolphin/idea_dolphin_module.iml");
    humanImlContent = loadBazelGeneratedFile("03_basic/human/idea_human_module.iml");
    gorillaImlContent = loadBazelGeneratedFile("03_basic/gorilla/idea_gorilla_module.iml");
    primateImlContent = loadBazelGeneratedFile("03_basic/primate/idea_primate_module.iml");
    mammalImlContent = loadBazelGeneratedFile("03_basic/mammal/idea_mammal_module.iml");

    modulesXmlContent = loadBazelGeneratedFile("03_basic/03modules_modules.xml");
  }

  @Test
  public void source_folders_sample() {
    // this scenario has one source file, in a non-maven-standard layout
    assertEquals(asList(
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./out/production/generated",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./out/test/generated_tests",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/main/java",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/test/java",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/main/resources",
      "file://$MODULE_DIR$/../../../../../03_basic/dolphin/./src/test/resources"),
      xpathList(dolphinImlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false", "true", "false", "true"),
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

  @Test
  public void modules_xml() {
    assertEquals(emptyList(),
      xpathList(modulesXmlContent, "/project/component/modules/module/fileurl").stream()
        .map(u -> u.split("/")[u.split("/").length-1])
        .collect(toList()));
  }
}
