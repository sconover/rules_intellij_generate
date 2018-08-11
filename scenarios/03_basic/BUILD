package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
    name="project_03",
    deps=[
        "//03_basic/dolphin:java_test_lib",
        "//03_basic/gorilla:java_test_lib",
        "//03_basic/human:java_test_lib",
    ],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[
        "//03_basic/dolphin:iml",
        "//03_basic/gorilla:iml",
        "//03_basic/human:iml",
        "//03_basic/mammal:iml",
        "//03_basic/primate:iml",
    ]
)
