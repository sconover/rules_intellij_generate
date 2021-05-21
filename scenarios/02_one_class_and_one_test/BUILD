load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//:__subpackages__"])

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

java_library(
    name = "java_lib",
    srcs = glob(["src/**/*.java"]),
)

java_library(
    name = "java_test_lib",
    srcs = glob(["test/**/*.java"]),
    deps = [":java_lib"] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name = "java_tests",
    java_package = "one_class_and_one_test",
    runtime_deps = [":java_test_lib"],
)

intellij_module(
    name = "iml",
    iml_type = "java-simple",
)

intellij_project(
    name = "project_02",
    iml_types_file = "//:iml_types.xml",
    modules = [":iml"],
    project_root_filegroup = "//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix = "intellij_project_files",
    test_lib_label_matchlist = ['{"label_name":"java_test_lib"}'],
    deps = [":java_test_lib"],
)
