package(default_visibility = ["//:__subpackages__"])

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

java_library(
    name="java_lib",
    srcs = glob(["src/**/*.java"]),
)

java_library(
    name="java_test_lib",
    srcs = glob(["test/**/*.java"]),
    deps=[":java_lib"] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name="java_tests",
    java_package="minor_features",
    runtime_deps=[":java_test_lib"]
)

filegroup(
    name="automatically_placed_intellij_project_files",
    srcs=glob(["intellij_project_files/**/*.xml"])
)

intellij_module(name="iml", iml_type="java-simple2", module_name_override="13_mymodulename")

intellij_project(
    name="project_13",
    deps=[":java_test_lib"],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    custom_substitutions={
      "MY_BICYCLE_COLOR": "red",
      "SOME_FILE": "$(location :some_file.txt)"
    },
    iml_types_file=":iml_types.xml",
    project_root_filegroup=":automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="13_minor_features/intellij_project_files",
    modules=[":iml"],
    tools=[":some_file.txt"],
)
