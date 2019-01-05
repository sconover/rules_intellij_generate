package(default_visibility = ["//:__subpackages__"])

load("@rules_multi_tsc//:def.bzl", "tsc")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

tsc(
    name="tsc",
    srcs=glob(["*.ts"]),
    deps=[
      "@node_modules_15_typescript_rules_multi_tsc//:default_typings",
      "//15_typescript_rules_multi_tsc/long-gen:tsc",
    ],

    node_executable="@node//:bin/node",
    tsc_script="@node_modules_15_typescript_rules_multi_tsc//:node_modules/typescript/lib/tsc.js",
    tsconfig_json="//15_typescript_rules_multi_tsc:tsconfig.json",
)

intellij_module(name="iml", iml_type="web-simple")
