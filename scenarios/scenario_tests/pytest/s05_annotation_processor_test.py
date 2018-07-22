import unittest
from scenario_test_support import *

class S05AnnotationProcessorTest(unittest.TestCase):

    archive = load_archive("05_annotation_processor/intellij_files")

    usage_iml_content = archive["05_annotation_processor/usage/usage.iml"]
    compiler_xml_content = archive["05_annotation_processor/.idea/compiler.xml"]

    def test_annotation_processor_config(self):
        self.assertEqual(
            ["foo_profile", "my_idea_auto_value_annotation_processor_profile"],
            xpath_attribute_list(self.compiler_xml_content, "./component/annotationProcessing/profile", "name"))

        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.usage_iml_content))
