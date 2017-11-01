package intellij_generate.iml;

import org.junit.jupiter.api.Test;

import static intellij_generate.iml.ImlContent.makeImlContent;
import static intellij_generate.testcommon.TestUtil.xpath;
import static intellij_generate.testcommon.TestUtil.xpathList;
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

  @Test
  public void makes_compile_lib_orderEntrys_in_order() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), emptyList(), emptyList(),
        asList(
          new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
          new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.COMPILE)));

    assertEquals(asList(
      "jar://foo/lib/path.jar!/",
      "jar://bar/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url"));
  }

  @Test
  public void makes_test_lib_orderEntrys_in_order() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), emptyList(), emptyList(),
        asList(
          new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.TEST),
          new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.TEST)));

    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    assertEquals(asList(
      "jar://foo/lib/path.jar!/",
      "jar://bar/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url"));
  }

  @Test
  public void libs_in_both_test_and_main_are_only_listed_in_main() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), emptyList(), emptyList(),
        asList(
          new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
          new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.TEST),
          new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
          new JarDependencyEntry("zzzlib", "zzz/lib/path.jar", JarDependencyEntry.Scope.TEST)));

    assertEquals(asList(
      "jar://foo/lib/path.jar!/",
      "jar://bar/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    assertEquals(asList(
      "jar://zzz/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url"));
  }

  @Test
  public void make_compile_module_dependencies() {
    String imlContent =
      makeImlContent(
        "../../this-is-the-content-dir",
        emptyList(), emptyList(), asList(
          new ModuleDependencyEntry("aaa-module", ModuleDependencyEntry.Scope.COMPILE),
          new ModuleDependencyEntry("bbb-module", ModuleDependencyEntry.Scope.COMPILE)
        ),
        emptyList());

    assertEquals(asList(
      "aaa-module",
      "bbb-module"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module']/@module-name"));
  }
}
