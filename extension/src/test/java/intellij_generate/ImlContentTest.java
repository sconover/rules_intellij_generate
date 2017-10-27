package intellij_generate;

import org.junit.jupiter.api.Test;

import static intellij_generate.ImlContent.makeImlContent;
import static intellij_generate.TestUtil.xpath;
import static intellij_generate.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImlContentTest {
  @Test
  public void makes_content_root() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), emptyList(), emptyList(), emptyList());

    assertEquals(
      "file://$MODULE_DIR$/../../this-is-the-content-dir",
      xpath(imlContent, "/module/component/content/@url"));
  }

  @Test
  public void makes_sources_roots_within_content_root() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        asList("foosrc/main/java", "barsrc/main/java"), emptyList(), emptyList(), emptyList());

    assertEquals(asList(
      "file://$MODULE_DIR$/../../this-is-the-content-dir/foosrc/main/java",
      "file://$MODULE_DIR$/../../this-is-the-content-dir/barsrc/main/java"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("false", "false"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  @Test
  public void makes_test_roots_within_content_root() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), asList("foosrc/test/java", "barsrc/test/java"), emptyList(), emptyList());

    assertEquals(asList(
      "file://$MODULE_DIR$/../../this-is-the-content-dir/foosrc/test/java",
      "file://$MODULE_DIR$/../../this-is-the-content-dir/barsrc/test/java"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@url"));

    assertEquals(
      asList("true", "true"),
      xpathList(imlContent, "/module/component/content/sourceFolder/@isTestSource"));
  }

  //TODO: what about src lib, test lib overlap? warn, w/ test-wins? how to communicate back warnings
  // ...just return them and use stderr in Main?
  //TODO: src libs, test libs, also overlap bug
}
