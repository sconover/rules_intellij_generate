package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")

intellij_project(
    name = "project_12",
    iml_types_file = "//:iml_types.xml",
    modules = [
        "//12_kotlin/child:iml",
        "//12_kotlin/parent:iml",
    ],
    project_root_filegroup = "//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix = "intellij_project_files",
    test_lib_label_matchlist = ['{"label_name":"kt_test_lib"}'],
    deps = [
        "//12_kotlin/child:kt_test_lib",
        "//12_kotlin/parent:kt_test_lib",
    ],
)
