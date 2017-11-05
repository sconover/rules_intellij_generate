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
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="child_tests",
    java_package="transitive_via_export.child",
    runtime_deps=[":child_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_child_module",
    compile_lib_deps = [":child_lib"],
    compile_module_deps = ["//04_transitive_via_export/parent:idea_parent_module"],
    test_lib_deps = [":child_tests_lib"],
)
