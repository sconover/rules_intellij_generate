package(default_visibility = ["//:__subpackages__"])

proto_library(
    name = "proto",
    srcs = ["src/main/proto/html_email.proto"],
    deps = [
        "//06_protobuf_messages/plain_email:proto",

        # bring in "well known proto" descriptor.proto.
        # This was enabled only recently, via this PR: https://github.com/google/protobuf/pull/3594
        "@com_google_protobuf//:descriptor_proto"
    ],
)

java_proto_library(
    name = "proto_java",
    deps = [":proto"],
)
