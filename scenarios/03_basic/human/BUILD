package(default_visibility = ["//03_basic:__subpackages__"])

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps=[
      "//03_basic/primate:lib",
    ]
)

java_library(
    name = "test_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":lib",
      "//03_basic/primate:lib",
      "//03_basic/mammal:lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="basic.human",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":lib"],
    compile_module_deps = [
      "//03_basic/primate:iml"
    ],
    test_lib_deps = [":test_lib"],
)
