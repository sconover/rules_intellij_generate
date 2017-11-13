package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl",
    "intellij_source_java_library",
    "MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP",
    "MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP")

intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP,
    deps=[
        "//06_protobuf_messages/plain_email:proto_java",
        "//06_protobuf_messages/html_email:proto_java",
        "@com_google_protobuf_java//:protobuf_java",
    ]
)

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP,
    deps=[
        ":lib",
        "//06_protobuf_messages/plain_email:proto_java",
        "//06_protobuf_messages/html_email:proto_java",

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
    java_package="protobuf_messages.usage",
    runtime_deps=[":test_lib"]
)
