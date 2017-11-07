package(default_visibility = ["//:__subpackages__"])

# these tests assert on iml and other build output, and therefore should really
# have some dependency on those things so that this doesn't execute out of order.
# but not sure what to do about that at this point.
java_library(
    name = "scenario_tests_lib",
    srcs = glob(["src/test/java/**/*.java"]),
    deps=[
      "@org_junit_jupiter_junit_jupiter_api//jar",
      "@org_opentest4j_opentest4j//jar",
    ],
)

load("@rules_junit5//:def.bzl", "junit5_all_in_package_test")
junit5_all_in_package_test(
    name="scenario_tests",
    java_package="intellij_generate.scenarios",
    runtime_deps=[":scenario_tests_lib"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_root_module",
    sources_roots = [],
    compile_lib_deps = [],
    test_sources_roots = ["src/test/java"],
    test_lib_deps = [":scenario_tests_lib"],
)

load("@rules_intellij_generate//:def.bzl", "intellij_modules_xml")
intellij_modules_xml(
    name = "scenariotestsmodules",
    deps = [":idea_root_module"]
)

load("@rules_intellij_generate//:def.bzl", "intellij_project")
intellij_project(
  name="scenariotestsproj",
  intellij_modules_xml=":scenariotestsmodules",
)