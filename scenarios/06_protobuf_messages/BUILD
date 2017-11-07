package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "06modules",
    deps = [
      "//06_protobuf_messages/usage:idea_usage_module",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="06proj",
  intellij_modules_xml=":06modules",
)