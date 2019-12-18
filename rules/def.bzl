load("@rules_intellij_generate//private:intellij_project.bzl", _intellij_project = "intellij_project")
load("@rules_intellij_generate//private:intellij_module.bzl", _intellij_module = "intellij_module")

intellij_project = _intellij_project
intellij_module = _intellij_module
