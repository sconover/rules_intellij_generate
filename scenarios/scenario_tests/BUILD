package(default_visibility = ["//scenario_tests:__subpackages__"])

# these tests assert on iml and other build output, and therefore should really
# have some dependency on those things so that this doesn't execute out of order.
# but not sure what to do about that at this point.

load("@rules_intellij_generate//:def.bzl",
    "intellij_source_java_library",
    "MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP")

intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP,
    deps=[
        "@org_junit_jupiter_junit_jupiter_api//jar",
        "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    test_java_source=":test_lib",
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [":iml"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  visibility=["//:__subpackages__"]
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="intellij_generate.scenarios",
    runtime_deps=[":test_lib"]
)
