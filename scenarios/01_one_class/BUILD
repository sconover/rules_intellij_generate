package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "one_class",
    srcs = glob(["src/**/*.java"]),
)

java_binary(
    name = "one_class_run",
    main_class="oneclass.A",
    srcs = glob(["src/**/*.java"]),
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_root_module",
    sources_roots = ["src"],
    compile_lib_deps = [":one_class"], # TODO: consider whether declaring this dependency explicitly is a good idea.
    test_sources_roots = [],
    test_lib_deps = [],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "01modules",
    deps = [":idea_root_module"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="01proj",
  intellij_modules_xml=":01modules",
)