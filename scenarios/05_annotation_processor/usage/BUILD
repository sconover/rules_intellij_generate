package(default_visibility = ["//05_annotation_processor:__subpackages__"])

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//05_annotation_processor/class_generator:annotation_processor",
        "//05_annotation_processor/text_file_generator:annotation_processor",
    ],
)

java_library(
    name = "test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="annotation_processor.usage",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":lib"],
    test_lib_deps = [":test_lib"],
    compile_module_deps = [
        "//05_annotation_processor/class_generator:iml",
        "//05_annotation_processor/text_file_generator:iml",
    ],
)
