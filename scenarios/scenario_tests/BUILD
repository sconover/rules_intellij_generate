load("@rules_python//python:defs.bzl", "py_test")

package(default_visibility = ["//scenario_tests:__subpackages__"])

py_test(
    name = "py_tests",
    srcs = glob(["pytest/**/*.py"]),
    data = [
        "//01_one_class:intellij_files",
        "//02_one_class_and_one_test:intellij_files",
        "//03_basic:intellij_files",
        "//04_transitive_via_export:intellij_files",
        "//05_annotation_processor:intellij_files",
        "//06_protobuf_messages:intellij_files",
        "//07_grpc:intellij_files",
        "//08_auto_value:intellij_files",
        "//09_python:intellij_files",
        "//12_kotlin:intellij_files",
        "//13_minor_features:install_intellij_files_script",
        "//13_minor_features:intellij_files",
        "//14_all_dependency_types:intellij_files",
    ],
    main = "all_tests.py",
)
