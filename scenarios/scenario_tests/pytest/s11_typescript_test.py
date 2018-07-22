import unittest
from scenario_test_support import *

class S11TypescriptTest(unittest.TestCase):

    archive = load_archive("11_typescript/intellij_files")

    iml_content = archive["11_typescript/11_typescript.iml"]

    def test_web_facet(self):
        self.assertEqual(["webfacet"], xpath_attribute_list(self.iml_content, "./component/facet", "name"))

