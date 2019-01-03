package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

exports_files(["tsconfig.json", "run_single_mocha_test.sh"])

intellij_module(name="iml", iml_type="web-simple")

filegroup(
    name="automatically_placed_intellij_project_files_for_ts",
    srcs=glob(["intellij_project_files/**/*.xml"])
)

filegroup(
    name="workspace_xml_fragments",
    srcs=glob(["workspace_xml_fragments/*.xml"])
)

intellij_project(
    name="project_10",
    iml_types_file="//:iml_types.xml",
    project_root_filegroup=":automatically_placed_intellij_project_files_for_ts",
    project_root_filegroup_ignore_prefix="15_typescript_rules_multi_tsc/intellij_project_files",
    workspace_xml_fragments_filegroup=":workspace_xml_fragments",
    deps=[
        "//15_typescript_rules_multi_tsc/long-gen:tsc",
        "//15_typescript_rules_multi_tsc/print-timestamp:tsc",
    ],
    modules=[
        ":iml",
        "//15_typescript_rules_multi_tsc/long-gen:iml",
        "//15_typescript_rules_multi_tsc/print-timestamp:iml",
    ],
    symlinks={
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_15_typescript_rules_multi_tsc/node_modules": "15_typescript_rules_multi_tsc/node_modules",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_15_typescript_rules_multi_tsc/package.json": "15_typescript_rules_multi_tsc/package.json",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_15_typescript_rules_multi_tsc/package-lock.json": "15_typescript_rules_multi_tsc/package-lock.json",
    },
)
