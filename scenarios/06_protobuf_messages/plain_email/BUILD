load("@rules_java//java:defs.bzl", "java_proto_library")

package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

proto_library(
    name = "proto",
    srcs = ["src/main/proto/plain_email.proto"],
)

java_proto_library(
    name = "java_proto",
    deps = [":proto"],
)

intellij_module(
    name = "iml",
    iml_type = "java-maven-style",
)
