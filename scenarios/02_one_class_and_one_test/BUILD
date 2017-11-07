package(default_visibility = ["//:__subpackages__"])

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
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="one_class_and_one_test_tests",
    java_package="one_class_and_one_test",
    runtime_deps=[":one_class_and_one_test_tests_lib"]
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
    name = "02modules",
    deps = [":idea_root_module"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="02proj",
  intellij_modules_xml=":02modules",
)