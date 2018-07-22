import unittest
from scenario_test_support import *

class S08AutoValueTest(unittest.TestCase):

    archive = load_archive("08_auto_value/intellij_files")

    compiler_xml_content = archive["08_auto_value/.idea/compiler.xml"]

    def test_source_folders(self):
        self.assertEqual([
            "foo_profile", "my_idea_auto_value_annotation_processor_profile"
        ], xpath_attribute_list(self.compiler_xml_content, "./component/annotationProcessing/profile", "name"))
