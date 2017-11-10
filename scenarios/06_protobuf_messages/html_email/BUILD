package(default_visibility = ["//:__subpackages__"])

proto_library(
    name = "proto",
    srcs = ["src/main/proto/html_email.proto"],
    deps = ["//06_protobuf_messages/plain_email:proto"]
)

java_proto_library(
    name = "proto_java",
    deps = [":proto"],
)
