package(default_visibility = ["//07_grpc:__subpackages__"])

proto_library(
    name = "proto",
    srcs = ["src/main/proto/fortune.proto"],
)

java_proto_library(
    name = "proto_java",
    deps = [":proto"],
)

# see https://groups.google.com/forum/#!topic/bazel-discuss/75DSeUTYBxE
load("@grpc_java//:java_grpc_library.bzl", "java_grpc_library")
java_grpc_library(
    name = "grpc",
    deps = [":proto_java"],
    srcs = [":proto"],
)

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
      ":grpc",
      ":proto_java",
      "@grpc_java//stub",
    ],
)

java_library(
    name = "test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":lib",
      ":proto_java",
      "@grpc_java//stub",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="fortune_grpc",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":lib"],
    test_lib_deps = [":test_lib"],
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