package(default_visibility = ["//03_basic:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_project",
    deps = [
      "//05_annotation_processor/usage:idea_usage_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_compiler_xml")
intellij_compiler_xml(
    name = "idea_annotation_processors",
    iml_target_to_annotation_profile = {
      "//05_annotation_processor/usage:idea_usage_module" : "foo_profile",
    }
)

