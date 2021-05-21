load("@rules_python//python:defs.bzl", "py_binary", "py_library", "py_test")

package(default_visibility = ["//private:__subpackages__"])

# === EXPORTS ===

py_binary(
    name = "intellij_generate_project_files",
    srcs = ["pysrc/rules_intellij_generate.py"],
    main = "rules_intellij_generate.py",
    visibility = ["//visibility:public"],
)

exports_files([
    "fswatch_and_install_intellij_files_mac.sh.template",
    "install_intellij_files.py.template",
])

# === PRIVATE ===

py_library(
    name = "py_lib",
    srcs = ["pysrc/rules_intellij_generate.py"],
)

py_test(
    name = "py_test",
    srcs = glob(["pytest/**/*.py"]),
    imports = ["pysrc"],
    main = "rules_intellij_generate_test.py",
    deps = [
        ":py_lib",
    ],
)
