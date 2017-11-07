package(default_visibility = ["//:__subpackages__"])

# see https://groups.google.com/forum/#!topic/bazel-discuss/75DSeUTYBxE
load("@grpc_java//:java_grpc_library.bzl", "java_grpc_library")
java_grpc_library(
    name = "fortune_java_grpc",
    deps = [":fortune_java_proto"],
    srcs = [":fortune_proto"],
)

java_proto_library(
    name = "fortune_java_proto",
    deps = [":fortune_proto"],
)

proto_library(
    name = "fortune_proto",
    srcs = ["src/main/proto/fortune.proto"],
)

java_library(
    name = "grpc_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
      ":fortune_java_grpc",
      ":fortune_java_proto",
      "@grpc_java//stub",
    ],
)

java_library(
    name = "grpc_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":grpc_lib",
      ":fortune_java_proto",
      "@grpc_java//stub",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="grpc_tests",
    java_package="fortune_grpc",
    runtime_deps=[":grpc_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_fortune_grpc_module",
    compile_lib_deps = [":grpc_lib"],
    test_lib_deps = [":grpc_tests_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "07modules",
    deps = [
      ":idea_fortune_grpc_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="07proj",
  intellij_modules_xml=":07modules",
)