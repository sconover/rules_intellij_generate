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
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="grandparent_tests",
    java_package="transitive_via_export.grandparent",
    runtime_deps=[":grandparent_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_grandparent_module",
    compile_lib_deps = [":grandparent_lib"],
    test_lib_deps = [":grandparent_tests_lib"],
)
