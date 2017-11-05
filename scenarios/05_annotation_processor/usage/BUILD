package(default_visibility = ["//05_annotation_processor:__subpackages__"])

java_library(
    name = "usage_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//05_annotation_processor/class_generator:class_generator_java_plugin",
        "//05_annotation_processor/text_file_generator:text_file_generator_java_plugin",
    ],
)

java_library(
    name = "usage_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":usage_lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="usage_tests",
    java_package="annotation_processor.usage",
    runtime_deps=[":usage_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_usage_module",
    compile_lib_deps = [":usage_lib"],
    test_lib_deps = [":usage_tests_lib"],
    compile_module_deps = [
        "//05_annotation_processor/class_generator:idea_class_generator_module",
        "//05_annotation_processor/text_file_generator:idea_text_file_generator_module",
    ],
)
