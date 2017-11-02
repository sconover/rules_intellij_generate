package(default_visibility = ["//03_basic:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_project",
    deps = [
      "//04_transitive_via_export/grandparent:idea_grandparent_module",
      "//04_transitive_via_export/parent:idea_parent_module",
      "//04_transitive_via_export/child:idea_child_module",
    ]
)