package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "05modules",
    deps = [
      "//05_annotation_processor/usage:idea_usage_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_compiler_xml")
intellij_compiler_xml(
    name = "05processors",
    iml_target_to_annotation_profile = {
      "//05_annotation_processor/usage:idea_usage_module" : "foo_profile",
    }
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="05proj",
  intellij_modules_xml=":05modules",
  intellij_compiler_xml=":05processors",
)