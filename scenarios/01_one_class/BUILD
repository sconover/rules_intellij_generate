package(default_visibility = ["//01_one_class:__subpackages__"])

# TODO: explicitly spcify run targets on the iml by way of something
# like run_targets = [java bin...]
# or even, a dict of target name to java_bin
java_binary(
    name = "run_A",
    main_class="oneclass.A",
    srcs = glob(["src/**/*.java"]),
)

load("@rules_intellij_generate//:def.bzl", "intellij_source_java_library")
intellij_source_java_library(
    name="lib",
    source_folder_to_wildcard_map={"src": "**/*.java"},
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name="iml",
    java_source=":lib",
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
