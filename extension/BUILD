package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "intellij_generate_lib",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@com_beust_jcommander//jar",
    ]
)

java_binary(
    name = "intellij_generate_iml",
    main_class = "intellij_generate.iml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":intellij_generate_lib",
    ]
)

java_binary(
    name = "intellij_generate_modules_xml",
    main_class = "intellij_generate.modules_xml.Main",
    visibility = ["//visibility:public"],
    runtime_deps = [
        ":intellij_generate_lib",
    ]
)

# TODO: some bazel+ junit5 magic can probably be done to make the tests run
# automatically (without explicitly specifying a package), or from a directory or something.

# TODO: the intellij integration has an inadvertent requirement that source_deps and
# test_deps be java_binary's, which is a bug. Once this is solved,
# split out the test libs from the core test stuff,
# such that we can have a separate java_test target.
# (As it stands, intellij_iml is not allowed to depend on a java_test
# target, or anyhing with testonly=True set.)

java_library(
  name="intellij_generate_tests_lib",
  srcs = glob(["src/test/java/**/*.java"]),
  deps=[
    ":intellij_generate_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
    "@org_junit_jupiter_junit_jupiter_engine//jar",
    "@org_junit_platform_junit_platform_commons//jar",
    "@org_junit_platform_junit_platform_console//jar",
    "@org_junit_platform_junit_platform_engine//jar",
    "@org_junit_platform_junit_platform_launcher//jar",
    "@org_junit_platform_junit_platform_runner//jar",
    "@org_opentest4j_opentest4j//jar",
  ],
)

java_test(
  name="intellij_generate_tests",
  main_class="org.junit.platform.console.ConsoleLauncher",
  args=[
    "--select-package intellij_generate",
    "--details verbose",
  ],
  runtime_deps=[
    ":intellij_generate_lib",
    ":intellij_generate_tests_lib",
    "@org_junit_jupiter_junit_jupiter_api//jar",
  ],
  use_testrunner=False,
)

# TODO: consider whether a convenient module method is in order.
# perhaps: intellij_java_module
# This would:
#   accept any number of src roots and dependencies
#   accept any number of test roots and dependencies
#   test classpath is test libs + class + src libs + classes
#   compose an intellij iml
# Also: make a junit 5 dependency filegroup

load("//:intellij_iml.bzl", "intellij_iml")
intellij_iml(
    name = "extension_root",
    source_deps = [":intellij_generate_lib"], # TODO: consider whether declaring this dependency explicitly is a good idea.
    test_deps = [":intellij_generate_tests_lib"],
)

load("//:intellij_modules_xml.bzl", "intellij_modules_xml_executable")
intellij_modules_xml_executable(
    name = "idea",
    deps = [":extension_root"]
)
