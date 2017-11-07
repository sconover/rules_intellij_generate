package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
      "//06_protobuf_messages/plain_email:proto_java",
      "//06_protobuf_messages/html_email:proto_java",
      "@com_google_protobuf_java//:protobuf_java",
    ],
)

java_library(
    name = "test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":lib",
      "//06_protobuf_messages/plain_email:proto_java",
      "//06_protobuf_messages/html_email:proto_java",

      "@com_google_protobuf_java//:protobuf_java",
      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="protobuf_messages.usage",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":lib"],
    test_lib_deps = [":test_lib"],
)
