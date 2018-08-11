import unittest
from scenario_test_support import *

class S14AllDependencyTypesTest(unittest.TestCase):

    archive = load_archive("14_all_dependency_types/intellij_files")

    child_data_iml_content = archive["14_all_dependency_types/child_data/child_data.iml"]
    child_dep_iml_content = archive["14_all_dependency_types/child_dep/child_dep.iml"]
    child_src_iml_content = archive["14_all_dependency_types/child_src/child_src.iml"]
    parent_iml_content = archive["14_all_dependency_types/parent/parent.iml"]

    child2_custom_iml_content = archive["14_all_dependency_types/child2_custom/child2_custom.iml"]

    def test_module_dependencies(self):
        self.assertEqual([],
                         xpath_attribute_list(self.parent_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["parent"],
                         xpath_attribute_list(self.child_src_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["parent"],
                         xpath_attribute_list(self.child_data_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))

        self.assertEqual(["parent2_custom"],
                         xpath_attribute_list(self.child2_custom_iml_content,
                                              "./component/orderEntry[@type='module']",
                                              "module-name"))
