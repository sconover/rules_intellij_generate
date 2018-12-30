package(default_visibility = ["//:__subpackages__"])

# load("@rules_intellij_generate//:def.bzl", "intellij_project")
# load("@rules_intellij_generate//:def.bzl", "intellij_module")
# load("@build_bazel_rules_nodejs//:defs.bzl", "nodejs_binary")
# load("@build_bazel_rules_nodejs//:defs.bzl", "nodejs_test")

# nodejs_binary(
#     name = "echo_main",
#     data = [
#         "@11_typescript_npm//:node_modules",
#         "src/echo_main.ts",
#     ],
#     entry_point = "rules_intellij_generate_scenarios/11_typescript/src/echo_main.ts",
#     node_modules = "@11_typescript_npm//:node_modules",
#     install_source_map_support = False,
# )

# filegroup(name = "echo_src", srcs = ["src/echo.ts"])
# filegroup(name = "echo_test_src", srcs = ["test/echo_test.ts"])
# filegroup(name = "mocha", srcs = [
#     "@11_typescript_npm//:node_modules/@types/mocha/index.d.ts",
#     "@11_typescript_npm//:node_modules/.bin/_mocha",
#     "@11_typescript_npm//:node_modules/mocha/index.js",
#     "@11_typescript_npm//:node_modules/mocha/mocha.js",
# ])

# sh_test(
#     name = "echo_test",
#     srcs = [":run_mocha_ts_test.sh"],
#     args = [
#         "11_typescript",
#         "external/11_typescript_npm/node_modules",
#         "$(location @nodejs//:bin/node)",
#         "$(location @11_typescript_npm//:node_modules/.bin/_mocha)",
#         "$(location :echo_test_src)"
#     ],
#     data = [
#         "@nodejs//:bin/node",
#         "@nodejs//:node_runfiles",
#         "@11_typescript_npm//:node_modules/.bin/_mocha",
#         ":mocha",
#         ":echo_src",
#         ":echo_test_src",
#     ],
# )

# filegroup(
#     name="automatically_placed_intellij_project_files_for_ts",
#     srcs=glob(["intellij_project_files/**/*.xml"])
# )

# filegroup(
#     name="workspace_xml_fragments",
#     srcs=glob(["workspace_xml_fragments/*.xml"])
# )

# intellij_module(name="iml", iml_type="web-simple")

# intellij_project(
#     name="project_11",
#     deps=[":echo_main"],
#     iml_types_file="//:iml_types.xml",
#     project_root_filegroup=":automatically_placed_intellij_project_files_for_ts",
#     project_root_filegroup_ignore_prefix="11_typescript/intellij_project_files",
#     workspace_xml_fragments_filegroup=":workspace_xml_fragments",
#     modules=[":iml"]
# )
