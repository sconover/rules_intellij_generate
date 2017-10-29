package(default_visibility = ["//src:__subpackages__"])

java_library(
    name = "one_class_and_one_test",
    srcs = glob(["src/**/*.java"]),
)

java_library(
    name = "one_class_and_one_test_tests_lib",
    srcs = glob(["test/**/*.java"]),
    deps=[
      ":one_class_and_one_test",

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
  name="one_class_and_one_test_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package one_class_and_one_test",
    "--details verbose",
  ],
  runtime_deps=[
    ":one_class_and_one_test_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_root_module",
    sources_roots = ["src"],
    compile_lib_deps = [":one_class_and_one_test"],
    test_sources_roots = ["test"],
    test_lib_deps = [":one_class_and_one_test_tests_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_project",
    deps = [":idea_root_module"]
)