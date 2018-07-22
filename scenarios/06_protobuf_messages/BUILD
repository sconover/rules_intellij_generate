package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")

intellij_project(
    name="project_06",
    deps=["//06_protobuf_messages/usage:java_test_lib"],
    build_managed_label_matchlist=['{"label_name":"java_proto"}'],
    test_lib_label_matchlist=['{"label_name":"java_test_lib"}'],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[
        "//06_protobuf_messages/html_email:iml",
        "//06_protobuf_messages/plain_email:iml",
        "//06_protobuf_messages/usage:iml",
    ]
)
