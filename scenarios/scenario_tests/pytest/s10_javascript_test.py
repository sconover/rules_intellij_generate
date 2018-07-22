import unittest
from scenario_test_support import *

class S10JavascriptTest(unittest.TestCase):

    archive = load_archive("10_javascript/intellij_files")

    iml_content = archive["10_javascript/10_javascript.iml"]

    def test_web_facet(self):
        self.assertEqual(["webfacet"], xpath_attribute_list(self.iml_content, "./component/facet", "name"))

