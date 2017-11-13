package(default_visibility = ["//02_one_class_and_one_test:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_source_java_library")

intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map={"src": "**/*.java"},
)

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map={"test": "**/*.java"},
    deps=[":lib"] + JUNIT5_MINIMAL_DEPS,
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name="iml",
    java_source=":lib",
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
  visibility=["//:__subpackages__"],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="one_class_and_one_test",
    runtime_deps=[":test_lib"]
)
