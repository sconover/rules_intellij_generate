package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
    name="project_14",
    deps=[
        "//14_all_dependency_types/child_data:child_data",
        "//14_all_dependency_types/child_dep:child_dep",
        "//14_all_dependency_types/child_src:child_src",

        "//14_all_dependency_types/child2_custom:from_child2",
        "//14_all_dependency_types/parent2_custom:lib",
    ],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[
        "//14_all_dependency_types/child_data:iml",
        "//14_all_dependency_types/child_dep:iml",
        "//14_all_dependency_types/child_src:iml",
        "//14_all_dependency_types/parent:iml",

        "//14_all_dependency_types/child2_custom:iml",
        "//14_all_dependency_types/parent2_custom:iml",
    ],
    module_dependency_matchlist = [
        '{"attr":"data"}',
        '{"attr":"deps"}',
        '{"attr":"srcs"}',

        '{"attr":"gorps"}',
    ],
)
