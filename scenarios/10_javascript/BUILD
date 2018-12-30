package(default_visibility = ["//:__subpackages__"])

# load("@rules_intellij_generate//:def.bzl", "intellij_project")
# load("@rules_intellij_generate//:def.bzl", "intellij_module")
# load("@build_bazel_rules_nodejs//:defs.bzl", "nodejs_binary")
# load("@build_bazel_rules_nodejs//:defs.bzl", "nodejs_test")

# nodejs_binary(
#     name = "echo_main",
#     data = [
#         "@10_javascript_npm//:node_modules",
#         "src/echo_main.js",
#     ],
#     entry_point = "rules_intellij_generate_scenarios/10_javascript/src/echo_main.js",
#     node_modules = "@10_javascript_npm//:node_modules",
#     install_source_map_support = False,
# )

# filegroup(name = "echo_src", srcs = ["src/echo.js"])
# filegroup(name = "echo_test_src", srcs = ["test/echo_test.js"])
# filegroup(name = "mocha", srcs = [
#     "@10_javascript_npm//:node_modules/.bin/_mocha",
#     "@10_javascript_npm//:node_modules/mocha/index.js",
#     "@10_javascript_npm//:node_modules/mocha/mocha.js",
# ])

# sh_test(
#     name = "echo_test",
#     srcs = [":run_mocha_test.sh"],
#     args = [
#         "external/10_javascript_npm/node_modules",
#         "$(location @nodejs//:bin/node)",
#         "$(location @10_javascript_npm//:node_modules/.bin/_mocha)",
#         "$(location :echo_test_src)"],
#     data = [
#         "@nodejs//:bin/node",
#         "@nodejs//:node_runfiles",
#         "@10_javascript_npm//:node_modules/.bin/_mocha",
#         ":mocha",
#         ":echo_src",
#         ":echo_test_src",
#     ],
# )

# filegroup(
#     name="automatically_placed_intellij_project_files_for_js",
#     srcs=glob(["intellij_project_files/**/*.xml"])
# )

# filegroup(
#     name="workspace_xml_fragments",
#     srcs=glob(["workspace_xml_fragments/*.xml"])
# )

# intellij_module(name="iml", iml_type="web-simple")

# intellij_project(
#     name="project_10",
#     deps=[":echo_main"],
#     iml_types_file="//:iml_types.xml",
#     project_root_filegroup=":automatically_placed_intellij_project_files_for_js",
#     project_root_filegroup_ignore_prefix="10_javascript/intellij_project_files",
#     workspace_xml_fragments_filegroup=":workspace_xml_fragments",
#     modules=[":iml"]
# )
