package(default_visibility = ["//src:__subpackages__"])

java_library(
    name = "one_class",
    srcs = glob(["src/**/*.java"]),
)

java_binary(
    name = "one_class_run",
    main_class="oneclass.A",
    srcs = glob(["src/**/*.java"]),
)

load("@rules_intellij_generate//:intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name = "idea_root_module",
    sources_roots = ["src"],
    source_deps = [":one_class"], # TODO: consider whether declaring this dependency explicitly is a good idea.
    test_sources_roots = [],
    test_deps = [],
)

load("@rules_intellij_generate//:intellij_modules_xml.bzl", "intellij_modules_xml_executable")
intellij_modules_xml_executable(
    name = "idea_project",
    deps = [":idea_root_module"]
)