load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//:__subpackages__"])

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

java_library(
    name = "java_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "//06_protobuf_messages/html_email:java_proto",
        "//06_protobuf_messages/plain_email:java_proto",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "java_test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps = [
        ":java_lib",
        "//06_protobuf_messages/plain_email:java_proto",
        "//06_protobuf_messages/html_email:java_proto",
        "@com_google_protobuf//:protobuf_java",
    ] + JUNIT5_MINIMAL_DEPS,
)

junit5_all_in_package_test(
    name = "java_tests",
    java_package = "protobuf_messages.usage",
    runtime_deps = [":java_test_lib"],
)

intellij_module(
    name = "iml",
    iml_type = "java-maven-style",
)
