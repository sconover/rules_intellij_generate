package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")
load("@rules_intellij_generate//:def.bzl", "intellij_project")

java_binary(
    name = "run_A",
    main_class="oneclass.A",
    srcs = glob(["src/**/*.java"]),
)

java_library(
    name="lib",
    srcs = glob(["src/**/*.java"]),
)

intellij_module(name="iml", iml_type="java-simple")

intellij_project(
    name="project_01",
    deps=[":lib"],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[":iml"]
)
