package(default_visibility = ["//:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_project")
load("@rules_intellij_generate//:def.bzl", "intellij_module")

py_library(
    name = "py_lib",
    srcs = ["src/echo.py"],
)

py_library(
    name = "py_test_lib",
    srcs = ["test/echo_test.py"],
    deps = [":py_lib"],
)

py_test(
    name = "echo_test",
    srcs = ["test/echo_test.py"],
    deps = [":py_lib"],
)

intellij_module(name="iml", iml_type="python-simple")

intellij_project(
    name="project_09",
    deps=[":py_test_lib"],
    iml_types_file="//:iml_types.xml",
    project_root_filegroup="//:automatically_placed_intellij_project_files",
    project_root_filegroup_ignore_prefix="intellij_project_files",
    modules=[":iml"]
)
