package intellij_generate.iml;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static intellij_generate.iml.ImlContent.makeImlContent;
import static intellij_generate.testcommon.TestUtil.xpath;
import static intellij_generate.testcommon.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImlContentTest {
  private String productionOutputDirRelativeToModuleRoot;
  private String testOutputDirRelativeToModuleRoot;
  private String generatedSourcesDirRelativeToProductionOutputDir;
  private String generatedTestSourcesDirRelativeToTestOutputDir;
  private List<String> sourcesRoots;
  private List<String> testSourcesRoots;
  private List<String> resourcesRoots;
  private List<ModuleDependencyEntry> moduleEntries;
  private List<JarDependencyEntry> libraryEntries;

  @BeforeEach
  public void beforeEach() {
    productionOutputDirRelativeToModuleRoot = "default_test/output/dir";
    testOutputDirRelativeToModuleRoot = "default_production/output/dir";
    generatedSourcesDirRelativeToProductionOutputDir = "default_generated_sources_subdir";
    generatedTestSourcesDirRelativeToTestOutputDir = "default_generated_test_sources_subdir";
    sourcesRoots = emptyList();
    testSourcesRoots = emptyList();
    resourcesRoots = emptyList();
    moduleEntries = emptyList();
    libraryEntries = emptyList();
  }

  private String makeImlContentForTest() {
    return makeImlContent(
      productionOutputDirRelativeToModuleRoot,
      testOutputDirRelativeToModuleRoot,
      generatedSourcesDirRelativeToProductionOutputDir,
      generatedTestSourcesDirRelativeToTestOutputDir,
      sourcesRoots,
      testSourcesRoots,
      resourcesRoots,
      moduleEntries,
      libraryEntries);
  }

  @Test
  public void makes_output_dirs() {
    productionOutputDirRelativeToModuleRoot = "production/output/dir";
    testOutputDirRelativeToModuleRoot = "test/output/dir";
    String imlContent = makeImlContentForTest();

    assertEquals(
      "file://$MODULE_DIR$/production/output/dir",
      xpath(imlContent, "/module/component/output/@url"));

    assertEquals(
      "file://$MODULE_DIR$/test/output/dir",
      xpath(imlContent, "/module/component/output-test/@url"));
  }

  @Test
  public void makes_content_root() {
    String imlContent = makeImlContentForTest();

    assertEquals(
      "file://$MODULE_DIR$",
      xpath(imlContent, "/module/component/content/@url"));
  }

  @Test
  public void makes_sources_roots_within_content_root() {
    sourcesRoots = asList("foosrc/main/java", "barsrc/main/java");
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "file://$MODULE_DIR$/foosrc/main/java",
      "file://$MODULE_DIR$/barsrc/main/java"),
      xpathList(imlContent, "/module/component/content/sourceFolder[not(@generated)]/@url"));

    assertEquals(
      asList("false", "false"),
      xpathList(imlContent, "/module/component/content/sourceFolder[not(@generated)]/@isTestSource"));
  }

  @Test
  public void makes_test_roots_within_content_root() {
    testSourcesRoots = asList("foosrc/test/java", "barsrc/test/java");
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "file://$MODULE_DIR$/foosrc/test/java",
      "file://$MODULE_DIR$/barsrc/test/java"),
      xpathList(imlContent, "/module/component/content/sourceFolder[not(@generated)]/@url"));

    assertEquals(
      asList("true", "true"),
      xpathList(imlContent, "/module/component/content/sourceFolder[not(@generated)]/@isTestSource"));
  }

  @Test
  public void makes_resources_roots_within_content_root() {
    resourcesRoots = asList("foosrc/main/resources", "barsrc/test/resources");
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "file://$MODULE_DIR$/foosrc/main/resources",
      "file://$MODULE_DIR$/barsrc/test/resources"),
      xpathList(imlContent, "/module/component/content/sourceFolder[@type='java-resource']/@url"));
  }

  @Test
  public void make_generated_source_folders_within_content_root() {
    productionOutputDirRelativeToModuleRoot = "production/output/dir"; // Now ignored, delete me.
    testOutputDirRelativeToModuleRoot = "test/output/dir";
    generatedSourcesDirRelativeToProductionOutputDir = "generated_sources_subdir";
    generatedTestSourcesDirRelativeToTestOutputDir = "generated_test_sources_subdir";
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "file://$MODULE_DIR$/production/output/dir/generated_sources_subdir",
      "file://$MODULE_DIR$/test/output/dir/generated_test_sources_subdir"),
      xpathList(imlContent, "/module/component/content/sourceFolder[@generated='true']/@url"));

    assertEquals(
      asList("false", "true"),
      xpathList(imlContent, "/module/component/content/sourceFolder[@generated='true']/@isTestSource"));
  }

  @Test
  public void makes_compile_lib_orderEntrys_in_order() {
    libraryEntries = asList(
      new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
      new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.COMPILE));
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "jar://foo/lib/path.jar!/",
      "jar://bar/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url"));
  }

  @Test
  public void makes_test_lib_orderEntrys_in_order() {
    libraryEntries = asList(
      new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.TEST),
      new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.TEST));
    String imlContent = makeImlContentForTest();

    assertEquals(emptyList(),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url"));

    assertEquals(asList(
      "jar://foo/lib/path.jar!/",
      "jar://bar/lib/path.jar!/"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url"));
  }

  @Test
  public void libs_in_both_test_and_main_are_only_listed_in_main() {
    libraryEntries = asList(
      new JarDependencyEntry("foolib", "foo/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
      new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.TEST),
      new JarDependencyEntry("barlib", "bar/lib/path.jar", JarDependencyEntry.Scope.COMPILE),
      new JarDependencyEntry("zzzlib", "zzz/lib/path.jar", JarDependencyEntry.Scope.TEST));
    String imlContent = makeImlContentForTest();

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
    moduleEntries = asList(
      new ModuleDependencyEntry("aaa-module", ModuleDependencyEntry.Scope.COMPILE),
      new ModuleDependencyEntry("bbb-module", ModuleDependencyEntry.Scope.COMPILE));
    String imlContent = makeImlContentForTest();

    assertEquals(asList(
      "aaa-module",
      "bbb-module"),
      xpathList(imlContent, "/module/component/orderEntry[@type='module']/@module-name"));
  }
}
