package(default_visibility = ["//04_transitive_via_export:__subpackages__"])

load("@rules_intellij_generate//:def.bzl",
    "intellij_source_java_library",
    "MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP",
    "MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP")

intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP,
    deps=[
        "@com_google_guava_guava//jar",
    ],
    exports = [
        "@com_google_guava_guava//jar"
    ],
)

intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP,
    deps=[
        ":lib",

        "@org_junit_jupiter_junit_jupiter_api//jar",
        "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    java_source=":lib",
    test_java_source=":test_lib",
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="transitive_via_export.grandparent",
    runtime_deps=[":test_lib"]
)
