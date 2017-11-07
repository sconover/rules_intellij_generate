package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "intellij_generate_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_beust_jcommander//jar",
    ]
)

java_binary(
    name = "intellij_generate_iml",
    main_class = "intellij_generate.iml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":intellij_generate_lib",
    ]
)

java_binary(
    name = "intellij_generate_modules_xml",
    main_class = "intellij_generate.modules_xml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":intellij_generate_lib",
    ]
)

java_binary(
    name = "intellij_generate_compiler_xml",
    main_class = "intellij_generate.compiler_xml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":intellij_generate_lib",
    ]
)

# TODO: some bazel+ junit5 magic can probably be done to make the tests run
# automatically (without explicitly specifying a package), or from a directory or something.

java_library(
  name="intellij_generate_tests_lib",
  srcs = glob(["src/test/java/**/*.java"]),
  deps=[
    ":intellij_generate_lib",

    "@org_junit_jupiter_junit_jupiter_api//jar",
    "@org_opentest4j_opentest4j//jar",
  ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="intellij_generate_tests",
    java_package="intellij_generate",
    runtime_deps=[
        ":intellij_generate_lib",
        ":intellij_generate_tests_lib",
    ]
)

exports_files(["install_intellij_project.sh.template"])

# use the rules developed in this project to also
# generate intellij files for the project.

load(":intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name = "idea_rules_private_module",
    compile_lib_deps = [":intellij_generate_lib"],
    test_lib_deps = [":intellij_generate_tests_lib"],
)

load(":intellij_modules_xml.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_rules_private_modules",
    deps = [":idea_rules_private_module"]
)

load(":intellij_project.bzl", "intellij_project")
intellij_project(
  name="rulesproj",
  intellij_modules_xml=":idea_rules_private_modules",
)