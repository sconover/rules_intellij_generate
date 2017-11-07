package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "usage_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
      "//06_protobuf_messages/plain_email:plain_email_java_proto",
      "//06_protobuf_messages/html_email:html_email_java_proto",
    ],
)

java_library(
    name = "usage_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":usage_lib",
      "//06_protobuf_messages/plain_email:plain_email_java_proto",
      "//06_protobuf_messages/html_email:html_email_java_proto",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="usage_tests",
    java_package="protobuf_messages.usage",
    runtime_deps=[":usage_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_usage_module",
    compile_lib_deps = [":usage_lib"],
    test_lib_deps = [":usage_tests_lib"],
)
