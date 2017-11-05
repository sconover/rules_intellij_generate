package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "04modules",
    deps = [
      "//04_transitive_via_export/grandparent:idea_grandparent_module",
      "//04_transitive_via_export/parent:idea_parent_module",
      "//04_transitive_via_export/child:idea_child_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="04proj",
  intellij_modules_xml=":04modules",
)