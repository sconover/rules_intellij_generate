package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

sh_library(
    name = "lib",
    srcs = ["parent2.sh"],
)

intellij_module(name="iml", iml_type="java-simple")
