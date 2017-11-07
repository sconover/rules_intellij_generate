package(default_visibility = ["//01_one_class:__subpackages__"])

java_library(
    name = "lib",
    srcs = glob(["src/**/*.java"]),
)

java_binary(
    name = "run_A",
    main_class="oneclass.A",
    srcs = glob(["src/**/*.java"]),
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    sources_roots = ["src"],
    compile_lib_deps = [":lib"],
    test_sources_roots = [],
    test_lib_deps = [],
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