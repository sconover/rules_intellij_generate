package(default_visibility = ["//:__subpackages__"])

load("@rules_multi_tsc//:def.bzl", "tsc")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

tsc(
    name="tsc",
    ts_path="lgen",
    srcs=glob(["src/*.ts"]),
    deps=[
      "@node_modules_15_typescript_rules_multi_tsc//:default_typings",
    ],

    node_executable="@node//:bin/node",
    tsc_script="@node_modules_15_typescript_rules_multi_tsc//:node_modules/typescript/lib/tsc.js",
    tsconfig_json="//15_typescript_rules_multi_tsc:tsconfig.json",
)

filegroup(name = "src_file", srcs = ["src/long-gen.ts"])
filegroup(name = "test_file", srcs = ["test/long-gen-test.ts"])

sh_library(
    name = "ts_lib",
    srcs = [":src_file"],
    data = [
        "@node//:bin/node",
        "@node_modules_15_typescript_rules_multi_tsc//:all_files",
        ":src_file",
    ],
)

sh_test(
    name = "ts_test",
    srcs = ["//15_typescript_rules_multi_tsc:run_single_mocha_test.sh"],
    deps = [":ts_lib"],
    args = [
        "external/node_modules_15_typescript_rules_multi_tsc/node_modules",
        "$(location @node//:bin/node)",
        "$(location //15_typescript_rules_multi_tsc:tsconfig.json)",
        "$(location @node_modules_15_typescript_rules_multi_tsc//:node_modules/mocha/bin/mocha)",
        "$(location :test_file)"
    ],
    data = [
        "@node//:bin/node",
        "//15_typescript_rules_multi_tsc:tsconfig.json",
        "@node_modules_15_typescript_rules_multi_tsc//:all_files",
        "@node_modules_15_typescript_rules_multi_tsc//:node_modules/mocha/bin/mocha",
        ":src_file",
        ":test_file",
    ],
)

intellij_module(name="iml", iml_type="web-simple")
