import unittest
from scenario_test_support import *

class S09PythonTest(unittest.TestCase):

    archive = load_archive("09_python/intellij_files")

    iml_content = archive["09_python/09_python.iml"]

    def test_python_facet(self):
        self.assertEqual(["Python"], xpath_attribute_list(self.iml_content, "./component/facet", "name"))
