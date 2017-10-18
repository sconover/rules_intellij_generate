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

# - create a tool dependency on @rules_intellij_generate//:intellij_generate
# - $(location @rules_intellij_generate//:intellij_generate) returns the path
#   to the java wrapper script that invokes intellij_generate.Main
# - The print statement from this Main class is directed into the output file, test_output
genrule(
    name = 'print_hello_genrule',
    tools = [
        "@rules_intellij_generate//:intellij_generate",
    ],
    outs = [
        'test_output',
    ],
    cmd = "$(location @rules_intellij_generate//:intellij_generate) > $(location test_output)",
)