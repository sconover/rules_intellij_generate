package(default_visibility = ["//02_one_class_and_one_test:__subpackages__"])

java_library(
    name = "lib",
    srcs = glob(["src/**/*.java"]),
)

java_library(
    name = "test_lib",
    srcs = glob(["test/**/*.java"]),
    deps=[
      ":lib",

      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="one_class_and_one_test",
    runtime_deps=[":test_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    sources_roots = ["src"],
    compile_lib_deps = [":lib"],
    test_sources_roots = ["test"],
    test_lib_deps = [":test_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [":iml"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  visibility=["//:__subpackages__"],
)