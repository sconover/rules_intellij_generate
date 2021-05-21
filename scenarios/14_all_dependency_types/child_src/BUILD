package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

sh_library(
    name = "child_src",
    srcs = ["//14_all_dependency_types/parent:parent.sh"],
)

intellij_module(
    name = "iml",
    iml_type = "java-simple",
)
