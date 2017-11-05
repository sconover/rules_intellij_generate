package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "03modules",
    deps = [
      "//03_basic/dolphin:idea_dolphin_module",
      "//03_basic/gorilla:idea_gorilla_module",
      "//03_basic/human:idea_human_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="03proj",
  intellij_modules_xml=":03modules",
)