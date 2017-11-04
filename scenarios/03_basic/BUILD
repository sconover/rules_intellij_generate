package(default_visibility = ["//03_basic:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "idea_project",
    deps = [
      "//03_basic/dolphin:idea_dolphin_module",
      "//03_basic/gorilla:idea_gorilla_module",
      "//03_basic/human:idea_human_module",
    ]
)