package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

filegroup(name = "src_file", srcs = ["src/sevens.js"])
filegroup(name = "test_file", srcs = ["test/sevens-test.js"])

# Aside: The js_lib target serves very little purpose other than to cause
# node dependencies to be exposed to intellij_project.
# The fact that this is necessary is a "wart":
# intellij_project may not depend on a testonly target (e.g. sh_test)
# unless it, itself, is marked testonly. That's a problem because
# then intellij files would only be generated as a result of bazel test runs,
# which I currently deem to be unacceptable. A better solution would be
# for bazel to allow aspect code to traverse testonly targets outside
# of test mode. Sigh.
sh_library(
    name = "js_lib",
    srcs = [":src_file"],
    data = [
        "@node//:bin/node",
        "@node_modules_10_javascript//:all_files",
        ":src_file",
    ],
)

sh_test(
    name = "js_test",
    srcs = [":run_single_mocha_test.sh"],
    deps = [":js_lib"],
    args = [
        "external/node_modules_10_javascript/node_modules",
        "$(location @node//:bin/node)",
        "$(location @node_modules_10_javascript//:node_modules/mocha/bin/mocha)",
        "$(location :test_file)"
    ],
    data = [
        "@node//:bin/node",
        "@node_modules_10_javascript//:all_files",
        "@node_modules_10_javascript//:node_modules/mocha/bin/mocha",
        ":src_file",
        ":test_file",
    ],
)

intellij_module(name="iml", iml_type="web-simple")

filegroup(
    name="automatically_placed_intellij_project_files_for_js",
    srcs=glob(["intellij_project_files/**/*.xml"])
)

filegroup(
    name="workspace_xml_fragments",
    srcs=glob(["workspace_xml_fragments/*.xml"])
)

intellij_project(
    name="project_10",
    iml_types_file="//:iml_types.xml",
    project_root_filegroup=":automatically_placed_intellij_project_files_for_js",
    project_root_filegroup_ignore_prefix="10_javascript/intellij_project_files",
    workspace_xml_fragments_filegroup=":workspace_xml_fragments",
    deps=[":js_lib"],
    modules=[":iml"],
    symlinks={
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_10_javascript/node_modules": "10_javascript/node_modules",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_10_javascript/package.json": "10_javascript/package.json",
      "${BAZEL_INFO_EXECUTION_ROOT}/external/node_modules_10_javascript/package-lock.json": "10_javascript/package-lock.json",
    },
)
