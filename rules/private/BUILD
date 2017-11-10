package(default_visibility = ["//private:__subpackages__"])

# === EXPORTS ===

java_binary(
    name = "intellij_generate_iml",
    main_class = "intellij_generate.iml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [":lib"]
)

java_binary(
    name = "intellij_generate_modules_xml",
    main_class = "intellij_generate.modules_xml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [":lib"]
)

java_binary(
    name = "intellij_generate_compiler_xml",
    main_class = "intellij_generate.compiler_xml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [":lib"]
)



# === PRIVATE ===

java_library(
    name = "lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_beust_jcommander//jar",
    ]
)

# TODO: some bazel+ junit5 magic can probably be done to make the tests run
# automatically (without explicitly specifying a package), or from a directory or something.

java_library(
  name="test_lib",
  srcs = glob(["src/test/java/**/*.java"]),
  deps=[
    ":lib",

    "@org_junit_jupiter_junit_jupiter_api//jar",
    "@org_opentest4j_opentest4j//jar",
  ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="tests",
    java_package="intellij_generate",
    runtime_deps=[
        ":lib",
        ":test_lib",
    ]
)

exports_files([
    "install_intellij_project.sh.template",
    "install_intellij_iml.sh.template",
])

# use the rules developed in this project to also
# generate intellij files for the project.

load(":intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":lib"],
    test_lib_deps = [":test_lib"],
)

load(":intellij_modules_xml.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [":iml"]
)

load(":intellij_project.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
)