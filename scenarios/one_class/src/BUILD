package(default_visibility = ["//src:__subpackages__"])

java_library(
    name = "one_class",
    srcs = glob(["**/*.java"]),
)

java_binary(
    name = "one_class_run",
    main_class="oneclass.A",
    srcs = ["oneclass/A.java"],
)

load("//rules:intellij_generate.bzl", "intellij_generate")

intellij_generate(
    name = "idea",
    lib = ":one_class",
)
