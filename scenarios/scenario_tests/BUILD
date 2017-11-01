package(default_visibility = ["//src:__subpackages__"])

# these tests assert on iml and other build output, and therefore should really
# have some dependency on those things so that this doesn't execute out of order.
# but not sure what to do about that at this point.
java_library(
    name = "scenario_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_junit_jupiter_junit_jupiter_engine//jar",
      "@org_junit_platform_junit_platform_commons//jar",
      "@org_junit_platform_junit_platform_console//jar",
      "@org_junit_platform_junit_platform_engine//jar",
      "@org_junit_platform_junit_platform_launcher//jar",
      "@org_junit_platform_junit_platform_runner//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

java_test(
  name="scenario_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package intellij_generate.scenarios",
    "--details verbose",
  ],
  runtime_deps=[
    ":scenario_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_root_module",
    sources_roots = [],
    compile_lib_deps = [],
    test_sources_roots = ["src/test/java"],
    test_lib_deps = [":scenario_tests_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_project",
    deps = [":idea_root_module"]
)