package(default_visibility = ["//03_basic:__subpackages__"])

java_library(
    name = "dolphin_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps=[
      "//03_basic/mammal:mammal_lib",
    ]
)

java_library(
    name = "dolphin_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":dolphin_lib",
      "//03_basic/mammal:mammal_lib",

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
  name="dolphin_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package basic.dolphin",
    "--details verbose",
  ],
  runtime_deps=[
    ":dolphin_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_dolphin_module",
    compile_lib_deps = [":dolphin_lib"],
    compile_module_deps = ["//03_basic/mammal:idea_mammal_module"],
    test_lib_deps = [":dolphin_tests_lib"],
)
