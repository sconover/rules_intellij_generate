package(default_visibility = ["//07_grpc:__subpackages__"])

proto_library(
    name = "proto",
    srcs = [
        "src/main/proto/fortune.proto"
    ],
    deps = [
        "@com_google_protobuf//:descriptor_proto",
    ]
)

java_proto_library(
    name = "proto_java",
    deps = [":proto"],
)

# see https://groups.google.com/forum/#!topic/bazel-discuss/75DSeUTYBxE
#
# from https://github.com/grpc/grpc-java/issues/2756
#  Args:
#     name: (str) A unique name for this rule. Required.
#     srcs: (list) a single proto_library target that contains the schema of the
#         service. Required.
#     deps: (list) a single java_proto_library target for the proto_library in
#         srcs.  Required.
#     flavor: (str) "normal" (default) for normal proto runtime. "lite"
#         for the lite runtime.
#     visibility: (list) the visibility list
load("@grpc_java//:java_grpc_library.bzl", "java_grpc_library")
java_grpc_library(
    name = "grpc",
    deps = [":proto_java"],
    srcs = [
        ":proto",
    ],
)

load("@rules_intellij_generate//:def.bzl",
    "intellij_source_java_library",
    "MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP",
    "MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP")

intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP,
    deps=[
        ":grpc",
        ":proto_java",
        "@grpc_java//stub",
    ]
)

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP,
    deps=[
        ":lib",
        ":proto_java",
        "@grpc_java//stub",
        "@com_google_protobuf_java//:protobuf_java",
    ] + JUNIT5_MINIMAL_DEPS,
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    java_source=":lib",
    test_java_source=":test_lib",
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="fortune_grpc",
    runtime_deps=[":test_lib"]
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