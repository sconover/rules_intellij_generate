package(default_visibility = ["//04_transitive_via_export:__subpackages__"])

java_library(
    name = "grandparent_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = ["@com_google_guava_guava//jar"],
    exports = ["@com_google_guava_guava//jar"],
)

java_library(
    name = "grandparent_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":grandparent_lib",

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
  name="grandparent_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package transitive_via_export.grandparent",
    "--details verbose",
  ],
  runtime_deps=[
    ":grandparent_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_grandparent_module",
    compile_lib_deps = [":grandparent_lib"],
    test_lib_deps = [":grandparent_tests_lib"],
)
