import unittest
from scenario_test_support import *

class S02OneClassAndOneTest(unittest.TestCase):

    archive = load_archive("02_one_class_and_one_test/intellij_files")

    iml_content = archive["02_one_class_and_one_test/02_one_class_and_one_test.iml"]

    def test_source_folders(self):
        self.assertEqual([
            "file://$MODULE_DIR$/out/production/generated",
            "file://$MODULE_DIR$/out/test/generated_tests",
            "file://$MODULE_DIR$/src",
            "file://$MODULE_DIR$/test"
        ], xpath_attribute_list(self.iml_content, "./component/content/sourceFolder", "url"))

        self.assertEqual(
            ["false", "true", "false", "true"],
            xpath_attribute_list(self.iml_content, "./component/content/sourceFolder", "isTestSource"))

    def test_output_folders(self):
        self.assertEqual(
            ["file://$MODULE_DIR$/out/production"],
            xpath_attribute_list(self.iml_content, "./component/output", "url"))

        self.assertEqual(
            ["file://$MODULE_DIR$/out/test"],
            xpath_attribute_list(self.iml_content, "./component/output-test", "url"))

    def test_libraries(self):
        self.assertEqual([], find_all_plain_jar_libraries(self.iml_content))
        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.iml_content))


