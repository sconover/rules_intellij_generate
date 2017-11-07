package(default_visibility = ["//07_grpc:__subpackages__"])

# see https://www.kchodorow.com/blog/2016/11/12/using-autovalue-with-bazel/
java_plugin(
    name = "autovalue_plugin",
    generates_api = 1,
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = ["@com_google_auto_value//jar"],
)

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_google_auto_value//jar",
        ":autovalue_plugin",
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
    java_package="house",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [
        ":lib",
        # ":autovalue_plugin",
    ],
    test_lib_deps = [":test_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [":iml"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_compiler_xml")
intellij_compiler_xml(
    name = "compiler_xml",
    iml_target_to_annotation_profile = {
      ":iml" : "my_idea_auto_value_annotation_processor_profile",
    }
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  intellij_compiler_xml=":compiler_xml",
  visibility=["//:__subpackages__"],
)