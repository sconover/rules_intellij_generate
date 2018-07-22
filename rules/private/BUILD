package(default_visibility = ["//private:__subpackages__"])

# === EXPORTS ===

py_binary(
    name = "intellij_generate_project_files",
    main = "rules_intellij_generate.py",
    srcs = ["pysrc/rules_intellij_generate.py"],
    visibility = ["//visibility:public"],
)

exports_files([
    "fswatch_and_install_intellij_files_mac.sh.template",
    "install_intellij_files.py.template"
])

# === PRIVATE ===

py_library(
    name = "py_lib",
    srcs = ["pysrc/rules_intellij_generate.py"],
)

py_test(
    name = "py_test",
    srcs = glob(["pytest/**/*.py"]),
    main = "rules_intellij_generate_test.py",
    imports = ["pysrc"],
    deps = [
        ":py_lib"
    ]
)
