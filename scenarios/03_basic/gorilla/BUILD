package(default_visibility = ["//03_basic:__subpackages__"])

java_library(
    name = "gorilla_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps=[
      "//03_basic/primate:primate_lib",
    ]
)

java_library(
    name = "gorilla_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      ":gorilla_lib",
      "//03_basic/primate:primate_lib",
      "//03_basic/mammal:mammal_lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="gorilla_tests",
    java_package="basic.gorilla",
    runtime_deps=[":gorilla_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_gorilla_module",
    compile_lib_deps = [":gorilla_lib"],
    compile_module_deps = ["//03_basic/primate:idea_primate_module"],
    test_lib_deps = [":gorilla_tests_lib"],
)
