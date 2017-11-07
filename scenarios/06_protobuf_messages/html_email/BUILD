package(default_visibility = ["//:__subpackages__"])

java_proto_library(
    name = "html_email_java_proto",
    deps = [":html_email_proto"],
)

proto_library(
    name = "html_email_proto",
    srcs = ["src/main/proto/html_email.proto"],
    deps = ["//06_protobuf_messages/plain_email:plain_email_proto"]
)