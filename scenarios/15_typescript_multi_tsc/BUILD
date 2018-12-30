package(default_visibility = ["//:__subpackages__"])

exports_files(["tsconfig.json"])

load("@rules_multi_tsc//:def.bzl", "rollup_js_source_bundle")
load("@rules_multi_tsc//:def.bzl", "rollup_js_vendor_bundle")
load("@rules_multi_tsc//:def.bzl", "uglify_es")

rollup_js_vendor_bundle(
    name="vendor_bundle",
    module_name="vendor",
    exports={
      "long": "_long",
      "bson": "_bson"
    },
    deps=[
      "@node_modules_archive//:long",
      "@node_modules_archive//:bson",
    ],

    node_executable="@node//:bin/node",
    rollup_script="@node_modules_archive//:node_modules/rollup/bin/rollup",
    rollup_plugins="@node_modules_archive//:rollup_plugins",
)

uglify_es(
    name="vendor_bundle_min",
    node_executable="@node//:bin/node",
    uglify_script="@node_modules_archive//:node_modules/uglify-es/bin/uglifyjs",
    rollup_dep=":vendor_bundle",
)

rollup_js_source_bundle(
    name="source_bundle",
    module_name="source",
    entrypoint_js_content="const x = require('lgen/long-gen'); console.log(x.longGen()); const y = require('ptstamp/print-timestamp'); y.printTimestamp()",
    tsc_dep="//15_typescript_multi_tsc/print-timestamp:tsc",
    globals={
      "long": "vendor._long",
      "bson": "vendor._bson"
    },

    node_executable="@node//:bin/node",
    rollup_script="@node_modules_archive//:node_modules/rollup/bin/rollup",
    rollup_plugins="@node_modules_archive//:rollup_plugins",
)

uglify_es(
    name="source_bundle_min",
    node_executable="@node//:bin/node",
    uglify_script="@node_modules_archive//:node_modules/uglify-es/bin/uglifyjs",
    rollup_dep=":source_bundle",
)
