package(default_visibility = ["//04_transitive_via_export:__subpackages__"])

java_library(
    name = "child_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = ["//04_transitive_via_export/parent:parent_lib"],
)

java_library(
    name = "child_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":child_lib",

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
  name="child_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package transitive_via_export.child",
    "--details verbose",
  ],
  runtime_deps=[
    ":child_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_child_module",
    compile_lib_deps = [":child_lib"],
    test_lib_deps = [":child_tests_lib"],
)
