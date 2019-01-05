package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

filegroup(name = "src_file", srcs = ["src/eights.ts"])
filegroup(name = "test_file", srcs = ["test/eights-test.ts"])

sh_library(
    name = "ts_lib",
    srcs = [":src_file"],
    data = [
        "@node//:bin/node",
        "@node_modules_11_typescript//:all_files",
        ":src_file",
    ],
)

sh_test(
    name = "ts_test",
    srcs = [":run_single_mocha_test.sh"],
    deps = [":ts_lib"],
    args = [
        "external/node_modules_11_typescript/node_modules",
        "$(location @node//:bin/node)",
        "$(location @node_modules_11_typescript//:node_modules/mocha/bin/mocha)",
        "$(location :test_file)"
    ],
    data = [
        "@node//:bin/node",
        "@node_modules_11_typescript//:all_files",
        "@node_modules_11_typescript//:node_modules/mocha/bin/mocha",
        ":src_file",
        ":test_file",
    ],
)

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
    project_root_filegroup_ignore_prefix="11_typescript/intellij_project_files",
    workspace_xml_fragments_filegroup=":workspace_xml_fragments",
    deps=[":ts_lib"],
    modules=[":iml"],
    symlinks={
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_11_typescript/node_modules": "11_typescript/node_modules",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_11_typescript/package.json": "11_typescript/package.json",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_11_typescript/package-lock.json": "11_typescript/package-lock.json",
    },
)
