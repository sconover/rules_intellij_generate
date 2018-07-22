package(default_visibility = ["//:__subpackages__"])

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
load("@rules_intellij_generate//:def.bzl", "intellij_module")
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "kt_lib",
    srcs = glob(["src/**/*.kt"]),
    deps=[
        "//12_kotlin/parent:kt_lib",
    ]
)

kt_jvm_library(
    name="kt_test_lib",
    srcs = glob(["test/**/*.kt"]),
    deps=[
        ":kt_lib",
    ] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name="kt_tests",
    java_package="kotlin_example",
    runtime_deps=[":kt_test_lib"]
)

intellij_module(name="iml", iml_type="kotlin-simple")