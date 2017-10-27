package(default_visibility = ["//03_basic:__subpackages__"])

java_library(
    name = "gorilla_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps=[
      "//03_basic/primate:primate_lib",
    ]
)

java_library(
    name = "gorilla_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":gorilla_lib",
      "//03_basic/primate:primate_lib",
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
  name="gorilla_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package basic.gorilla",
    "--details verbose",
  ],
  runtime_deps=[
    ":gorilla_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
  size="small",
)

load("@rules_intellij_generate//:intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name = "idea_gorilla_module",
    compile_lib_deps = [":gorilla_lib"],
    compile_module_deps = ["//03_basic/primate:idea_primate_module"],
    test_lib_deps = [":gorilla_tests_lib"],
)
