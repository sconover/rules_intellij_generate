java_binary(
    name = "intellij_generate",
    main_class = "intellij_generate.Main",
    srcs = glob(["src/main/java/**/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        "@com_beust_jcommander//jar",
    ]
)

load("//:intellij_iml.bzl", "intellij_iml")

intellij_iml(
    name = "extension_root",
    deps = ["//:intellij_generate"]
)

