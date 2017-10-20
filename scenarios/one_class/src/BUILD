package(default_visibility = ["//src:__subpackages__"])

java_library(
    name = "one_class",
    srcs = glob(["**/*.java"]),
)

java_binary(
    name = "one_class_run",
    main_class="oneclass.A",
    srcs = glob(["**/*.java"]),
)

load("@io_bazel_rules_intellij_generate//intellij_generate:intellij_generate.bzl", "intellij_generate")
intellij_generate(name="intellij_generate_one_class")

load("@io_bazel_rules_intellij_generate//intellij_generate:intellij_generate.bzl", "print_hello")
print_hello(name="hello_from_one_class")