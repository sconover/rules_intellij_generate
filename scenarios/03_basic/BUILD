package(default_visibility = ["//03_basic:__subpackages__"])

load("@rules_intellij_generate//:intellij_modules_xml.bzl", "intellij_modules_xml_executable")
intellij_modules_xml_executable(
    name = "idea_project",
    deps = [
      "//03_basic/dolphin:idea_dolphin_module",
      "//03_basic/gorilla:idea_gorilla_module",
      "//03_basic/human:idea_human_module",
      "//03_basic/mammal:idea_mammal_module", # TODO: should this have to be listed here, or should we expect this to walk transitive deps?
      "//03_basic/primate:idea_primate_module", # TODO: ibid
    ]
)