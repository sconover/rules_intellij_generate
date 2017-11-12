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

# use the rules developed in this project to also
# generate intellij files for the project.

load(":intellij_source.bzl",
    "intellij_source_java_library",
    "MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP",
    "MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP")

intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP,
    deps = [
        "@com_beust_jcommander//jar",
    ]
)

load("@rules_junit5//:def.bzl", "JUNIT5_MINIMAL_DEPS")
intellij_source_java_library(
    name="test_lib",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP,
    deps=[":lib"] + JUNIT5_MINIMAL_DEPS,
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

load(":intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name="iml",
    java_source=":lib",
    test_java_source=":test_lib",
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