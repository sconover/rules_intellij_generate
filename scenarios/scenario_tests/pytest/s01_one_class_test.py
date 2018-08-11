import unittest
from scenario_test_support import *

class S01OneClassTest(unittest.TestCase):

    archive = load_archive("01_one_class/intellij_files")

    iml_content = archive["01_one_class/01_one_class.iml"]
    modules_xml_content = archive["01_one_class/.idea/modules.xml"]

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
        self.assertEqual(
            [],
            xpath_attribute_list(self.iml_content,
                                 "./component/orderEntry[@type='module-library']/library/CLASSES/root",
                                 "url"))

    def test_modules_xml(self):
        self.assertEqual(["file://$PROJECT_DIR$/01_one_class.iml"],
                         xpath_attribute_list(self.modules_xml_content, "./component/modules/module", "fileurl"))

