package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")
load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")

# see https://www.kchodorow.com/blog/2016/11/12/using-autovalue-with-bazel/
java_plugin(
    name = "autovalue_plugin",
    generates_api = 1,
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = ["@com_google_auto_value//jar"],
)

java_library(
    name="java_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps=[
        ":autovalue_plugin",

        "@com_google_auto_value//jar",
    ],
)

java_library(
    name="java_test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[":java_lib"] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name="java_tests",
    java_package="house",
    runtime_deps=[":java_test_lib"]
)

intellij_module(name="iml", iml_type="java-maven-style")

intellij_project(
    name="project_08",
    deps=[":java_test_lib"],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[":iml"]
)
