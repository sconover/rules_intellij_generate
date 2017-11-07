package(default_visibility = ["//03_basic:__subpackages__"])

java_library(
    name = "mammal_lib",
    srcs = glob(["src/main/java/**/*.java"]),
)

java_library(
    name = "mammal_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":mammal_lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="mammal_tests",
    java_package="basic.mammal",
    runtime_deps=[":mammal_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_mammal_module",
    compile_lib_deps = [":mammal_lib"],
    test_lib_deps = [":mammal_tests_lib"],
)
