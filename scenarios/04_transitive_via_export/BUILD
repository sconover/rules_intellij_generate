package(default_visibility = ["//04_transitive_via_export:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [
      "//04_transitive_via_export/grandparent:iml",
      "//04_transitive_via_export/parent:iml",
      "//04_transitive_via_export/child:iml",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  visibility=["//:__subpackages__"],
)