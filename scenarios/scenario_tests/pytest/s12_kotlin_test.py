import unittest
from scenario_test_support import *

class S12KotlinTest(unittest.TestCase):

    archive = load_archive("12_kotlin/intellij_files")

    parent_iml_content = archive["12_kotlin/parent/parent.iml"]
    child_iml_content = archive["12_kotlin/child/child.iml"]

    def test_kotlin_facet(self):
        self.assertEqual(["Kotlin"], xpath_attribute_list(self.parent_iml_content, "./component/facet", "name"))
        self.assertEqual(["Kotlin"], xpath_attribute_list(self.child_iml_content, "./component/facet", "name"))


    def test_source_folders(self):
        expected = [
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-19.0.jar!/",

            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-compiler.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-reflect.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-runtime.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-script-runtime.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-stdlib-jdk7.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-stdlib-jdk8.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_github_jetbrains_kotlin/lib/kotlin-stdlib.jar!/",
        ]

        self.assertEqual(expected, find_all_plain_jar_libraries(self.child_iml_content))
        self.assertEqual(expected, find_all_plain_jar_libraries(self.parent_iml_content))

    def test_module_dependencies(self):
        self.assertEqual(["parent"],
                         xpath_attribute_list(self.child_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual([],
                         xpath_attribute_list(self.parent_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))
