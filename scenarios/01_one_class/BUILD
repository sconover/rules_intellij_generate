load("@rules_java//java:defs.bzl", "java_binary", "java_library")

package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")
load("@rules_intellij_generate//:def.bzl", "intellij_project")

java_binary(
    name = "run_A",
    srcs = glob(["src/**/*.java"]),
    main_class = "oneclass.A",
)

java_library(
    name = "lib",
    srcs = glob(["src/**/*.java"]),
)

intellij_module(
    name = "iml",
    iml_type = "java-simple",
)

intellij_project(
    name = "project_01",
    iml_types_file = "//:iml_types.xml",
    modules = [":iml"],
    project_root_filegroup = "//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix = "intellij_project_files",
    deps = [":lib"],
)
