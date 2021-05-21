package(default_visibility = ["//:__subpackages__"])

load("@rules_multi_tsc//:def.bzl", "tsc")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

tsc(
    name = "tsc",
    srcs = glob(["src/*.ts"]),
    node_executable = "@node//:bin/node",
    ts_path = "lgen",
    tsc_script = "@node_modules_15_typescript_rules_multi_tsc//:node_modules/typescript/lib/tsc.js",
    tsconfig_json = "//15_typescript_rules_multi_tsc:tsconfig.json",
    deps = [
        "@node_modules_15_typescript_rules_multi_tsc//:default_typings",
    ],
)

filegroup(
    name = "src_file",
    srcs = ["src/long-gen.ts"],
)

filegroup(
    name = "test_file",
    srcs = ["test/long-gen-test.ts"],
)

sh_library(
    name = "ts_lib",
    srcs = [":src_file"],
    data = [
        ":src_file",
        "@node//:bin/node",
        "@node_modules_15_typescript_rules_multi_tsc//:all_files",
    ],
)

sh_test(
    name = "ts_test",
    srcs = ["//15_typescript_rules_multi_tsc:run_single_mocha_test.sh"],
    args = [
        "external/node_modules_15_typescript_rules_multi_tsc/node_modules",
        "$(location @node//:bin/node)",
        "$(location //15_typescript_rules_multi_tsc:tsconfig.json)",
        "$(location @node_modules_15_typescript_rules_multi_tsc//:node_modules/mocha/bin/mocha)",
        "$(location :test_file)",
    ],
    data = [
        ":src_file",
        ":test_file",
        "//15_typescript_rules_multi_tsc:tsconfig.json",
        "@node//:bin/node",
        "@node_modules_15_typescript_rules_multi_tsc//:all_files",
        "@node_modules_15_typescript_rules_multi_tsc//:node_modules/mocha/bin/mocha",
    ],
    deps = [":ts_lib"],
)

intellij_module(
    name = "iml",
    iml_type = "web-simple",
)
