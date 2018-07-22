import unittest
from scenario_test_support import *

class S03BasicTest(unittest.TestCase):

    archive = load_archive("03_basic/intellij_files")

    dolphin_iml_content = archive["03_basic/dolphin/dolphin.iml"]
    human_iml_content = archive["03_basic/human/human.iml"]
    gorilla_iml_content = archive["03_basic/gorilla/gorilla.iml"]
    primate_iml_content = archive["03_basic/primate/primate.iml"]
    mammal_iml_content = archive["03_basic/mammal/mammal.iml"]

    modules_xml_content = archive["03_basic/.idea/modules.xml"]

    def test_source_folders_sample(self):
        self.assertEqual([
            "file://$MODULE_DIR$/out/production/generated",
            "file://$MODULE_DIR$/out/test/generated_tests",
            "file://$MODULE_DIR$/src/main/java",
            "file://$MODULE_DIR$/src/test/java"
        ], xpath_attribute_list(self.dolphin_iml_content, "./component/content/sourceFolder", "url"))

        self.assertEqual(
            ["false", "true", "false", "true"],
            xpath_attribute_list(self.dolphin_iml_content, "./component/content/sourceFolder", "isTestSource"))

    def test_output_folders(self):
        self.assertEqual(
            ["file://$MODULE_DIR$/out/production"],
            xpath_attribute_list(self.dolphin_iml_content, "./component/output", "url"))

        self.assertEqual(
            ["file://$MODULE_DIR$/out/test"],
            xpath_attribute_list(self.dolphin_iml_content, "./component/output-test", "url"))

    def test_libraries(self):
        self.assertEqual([], find_all_plain_jar_libraries(self.dolphin_iml_content))
        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.dolphin_iml_content))

    def test_module_dependencies(self):
        self.assertEqual([],
                         xpath_attribute_list(self.mammal_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["mammal"],
                         xpath_attribute_list(self.primate_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["mammal"],
                         xpath_attribute_list(self.dolphin_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["mammal", "primate"],
                         xpath_attribute_list(self.human_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["mammal", "primate"],
                         xpath_attribute_list(self.gorilla_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

    def test_modules_xml(self):
        self.assertEqual([
            "file://$PROJECT_DIR$/dolphin/dolphin.iml",
            "file://$PROJECT_DIR$/gorilla/gorilla.iml",
            "file://$PROJECT_DIR$/human/human.iml",
            "file://$PROJECT_DIR$/mammal/mammal.iml",
            "file://$PROJECT_DIR$/primate/primate.iml",
        ], xpath_attribute_list(self.modules_xml_content, "./component/modules/module", "fileurl"))
