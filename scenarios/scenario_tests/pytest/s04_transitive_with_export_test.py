import unittest
from scenario_test_support import *

class S04TransitiveWithExportTest(unittest.TestCase):

    archive = load_archive("04_transitive_via_export/intellij_files")

    grandparent_iml_content = archive["04_transitive_via_export/grandparent/grandparent.iml"]
    parent_iml_content = archive["04_transitive_via_export/parent/parent.iml"]
    child_iml_content = archive["04_transitive_via_export/child/child.iml"]

    def test_compile_deps_dont_show_up_in_test_deps(self):
        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.child_iml_content))

    def test_sample_jar_dependencies(self):
        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-19.0.jar!/"
        ], find_all_plain_jar_libraries(self.grandparent_iml_content))

        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-19.0.jar!/"
        ], find_all_plain_jar_libraries(self.parent_iml_content))

        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-19.0.jar!/"
        ], find_all_plain_jar_libraries(self.child_iml_content))
