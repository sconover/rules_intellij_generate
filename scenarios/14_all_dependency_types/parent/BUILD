package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

sh_library(
    name = "parent",
    srcs = ["parent.sh"],
    data = ["parent.txt"],
)

intellij_module(name="iml", iml_type="java-simple")
