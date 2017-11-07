package(default_visibility = ["//05_annotation_processor:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [
      "//05_annotation_processor/usage:iml",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_compiler_xml")
intellij_compiler_xml(
    name = "compiler_xml",
    iml_target_to_annotation_profile = {
      "//05_annotation_processor/usage:iml" : "foo_profile",
    }
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  intellij_compiler_xml=":compiler_xml",
  visibility=["//:__subpackages__"],
)