package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")

intellij_project(
    name="project_04",
    deps=["//04_transitive_via_export/child:java_test_lib"],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[
        "//04_transitive_via_export/grandparent:iml",
        "//04_transitive_via_export/parent:iml",
        "//04_transitive_via_export/child:iml",
    ]
)
