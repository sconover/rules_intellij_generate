package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")
load("//14_all_dependency_types:custom2.bzl", "custom2")

custom2(
    name = "from_child2",
    gorps = ["//14_all_dependency_types/parent2_custom:lib"]
)

intellij_module(name="iml", iml_type="java-simple")
