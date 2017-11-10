package(default_visibility = ["//06_protobuf_messages:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "modules_xml",
    deps = [
      "//06_protobuf_messages/html_email:iml",
      "//06_protobuf_messages/plain_email:iml",
      "//06_protobuf_messages/usage:iml",
    ]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="idea_project",
  intellij_modules_xml=":modules_xml",
  visibility=["//:__subpackages__"],
)