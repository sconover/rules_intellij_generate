package(default_visibility = ["//:__subpackages__"])

proto_library(
    name = "proto",
    srcs = ["src/main/proto/plain_email.proto"],
)

java_proto_library(
    name = "proto_java",
    deps = [":proto"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(name = "iml")
