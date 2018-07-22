package(default_visibility = ["//:__subpackages__"])

load("@grpc_java//:java_grpc_library.bzl", "java_grpc_library")
load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

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
    name = "java_proto",
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
java_grpc_library(
    name = "java_grpc",
    deps = [":java_proto"],
    srcs = [
        ":proto",
    ],
)

java_library(
    name="java_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        ":java_proto",
        ":java_grpc",

        "@grpc_java//stub",
        "@com_google_protobuf_java//:protobuf_java",
    ]
)

java_library(
    name="java_test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
        ":java_lib",
        ":java_proto",

        "@grpc_java//stub",
        "@com_google_protobuf_java//:protobuf_java",
    ] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name="java_tests",
    java_package="fortune_grpc",
    runtime_deps=[":java_test_lib"]
)

intellij_module(name="iml", iml_type="java-maven-style")

intellij_project(
    name="project_07",
    deps=[":java_test_lib"],
    build_managed_label_matchlist=['{"label_name":"java_proto"}', '{"label_name":"java_grpc"}'],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[":iml"]
)
