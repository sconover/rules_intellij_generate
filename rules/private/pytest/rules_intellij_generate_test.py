import unittest

from rules_intellij_generate import *


class RulesIntellijGenerateTest(unittest.TestCase):

    def test_xml_indent(self):
        a_element = parse_xml("<a></a>")
        a_element.append(parse_xml("<z>1</z>"))
        a_element.append(parse_xml("<x><y>9</y></x>"))
        a_element.append(parse_xml("<b>2</b>"))
        a_element.append(parse_xml("<c>3</c>"))
        a_element.append(parse_xml("<d>4</d>"))
        a_element.append(parse_xml("<e><f><g>5</g></f></e>"))
        self.assertEqual(
            "\n".join(
                [
                    "<a>",
                    "  <z>1</z>",
                    "  <x>",
                    "    <y>9</y>",
                    "  </x>",
                    "  <b>2</b>",
                    "  <c>3</c>",
                    "  <d>4</d>",
                    "  <e>",
                    "    <f>",
                    "      <g>5</g>",
                    "    </f>",
                    "  </e>",
                    "</a>",
                    ""
                ]), ET.tostring(xml_indent(a_element)))

    def test_iml_paths(self):
        root_intellij_module = {
            "bazel_package": "",
            "module_name": "foo",
            "iml_type": "java"
        }

        regular_intellij_module = {
            "bazel_package": "some_package/bar",
            "iml_type": "java"
        }

        self.assertEqual("foo.iml", iml_path_from_declared_intellij_module(root_intellij_module, "foo"))
        self.assertEqual("some_package/bar/bar.iml", iml_path_from_declared_intellij_module(regular_intellij_module, ""))

    def test_check_unique_intellij_module_names(self):
        check_unique_intellij_module_names([
            {"bazel_package": "a/foo"},
            {"bazel_package": "b/bar"},
        ], "the_root_package")

        check_unique_intellij_module_names([
            {"bazel_package": "a/foo"},
            {"bazel_package": "b/foo", "module_name_override": "bar"},
        ], "the_root_package")

        def do_non_unique_module_names():
            check_unique_intellij_module_names([
                {"bazel_package": "a/foo"},
                {"bazel_package": "b/foo"},
            ], "the_root_package")

        self.assertRaises(DuplicateModuleNameException, do_non_unique_module_names)

        def do_override_to_non_unique_module_names():
            check_unique_intellij_module_names([
                {"bazel_package": "a/foo"},
                {"bazel_package": "b/bar", "module_name_override": "foo"},
            ], "the_root_package")

        self.assertRaises(DuplicateModuleNameException, do_override_to_non_unique_module_names)


    def test_basic_composer_and_conversion_to_xml(self):
        iml_types_xml = """
<?xml version='1.0' encoding='UTF-8'?>
<iml-types>
    <iml-type name=\"java\">
        <module>
            <component name=\"NewModuleRootManager\">
                <content>
                    <existing />
                </content>
            </component>
        </module>
    </iml-type>
</iml-types>
""".strip()

        expected_root_iml_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
  </component>
</module>
""".strip() + "\n"

        declared_intellij_module = {
            "bazel_package": "some_package/bar",
            "iml_type": "java"
        }

        self.assertEqual(
            {"some_package/bar/bar.iml": expected_root_iml_content},
            composers_to_xmls(
                make_bazel_package_iml_composers(
                    iml_types_xml,
                    [declared_intellij_module]), "root_package"))

    def test_insert_jar_dep_into_iml_element_as_module_library(self):
        iml_base_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
""".strip() + "\n"

        expected_content_1 = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry forTests="false" type="sourceFolder" />
    <orderEntry type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/lib.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
  </component>
</module>
""".strip() + "\n"

        element1 = parse_xml(iml_base_content)
        insert_jar_dep_into_iml_element_as_module_library(
            element1,
            "some/lib.jar",
            is_test_mode=False)

        self.assertEqual(expected_content_1, convert_xml_element_to_pretty_printed_xml_string(element1))

        expected_content_2 = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry forTests="false" type="sourceFolder" />
    <orderEntry scope="TEST" type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/lib.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
  </component>
</module>
""".strip() + "\n"

        element2 = parse_xml(iml_base_content)
        insert_jar_dep_into_iml_element_as_module_library(
            element2,
            "some/lib.jar",
            is_test_mode=True)

        self.assertEqual(expected_content_2, convert_xml_element_to_pretty_printed_xml_string(element2))

    def test_add_jar_libraries(self):
        iml_base_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name="NewModuleRootManager">
    <content>
      <existing />
    </content>
  </component>
</module>
""".strip() + "\n"

        element = parse_xml(iml_base_content)
        insert_all_jar_libraries(
            {"foo": IntellijModuleComposer({}, element)},
            ['{"label_name":"managed_by_build_tool"}'],
            ['{"package":"fo*","label_name":"some_test_lib_label_name"}'],
            [
                {
                    "bazel_package": "foo",
                    "label_name": "not_managed_by_build_tool",
                    "test_mode": True,
                    "generated_by_build": True,
                    "relative_jar_path": "some/relative/path/off/the/execroot/a-was-generated-by-the-build.jar",
                    "owner_workspace_root": ""
                },
                {
                    "bazel_package": "foo",
                    "label_name": "some_test_lib_label_name",
                    "test_mode": True,
                    "generated_by_build": False,
                    "relative_jar_path": "some/relative/path/off/the/execroot/b-show-up-in-test-mode-only.jar",
                    "owner_workspace_root": "external"
                },
                {
                    "bazel_package": "foo",
                    "label_name": "some_label_name",
                    "test_mode": False,
                    "generated_by_build": False,
                    "relative_jar_path": "some/relative/path/off/the/execroot/c-show-up-in-non-test-mode-only.jar",
                    "owner_workspace_root": "external"
                },
                {
                    "bazel_package": "foo",
                    "label_name": "some_test_lib_label_name",
                    "test_mode": True,
                    "generated_by_build": False,
                    "relative_jar_path": "some/relative/path/off/the/execroot/d-show-up-in-both-modes.jar",
                    "owner_workspace_root": "external"
                },
                {
                    "bazel_package": "foo",
                    "label_name": "some_label_name",
                    "test_mode": False,
                    "generated_by_build": False,
                    "relative_jar_path": "some/relative/path/off/the/execroot/d-show-up-in-both-modes.jar",
                    "owner_workspace_root": "external"
                },
                {
                    "bazel_package": "foo",
                    "label_name": "managed_by_build_tool",
                    "test_mode": False,
                    "generated_by_build": True,
                    "relative_jar_path": "some/relative/path/off/the/execroot/e-was-generated-by-the-build.jar",
                    "owner_workspace_root": ""
                },
            ])

        # - libs generated by the build:
        #     ...that are generated outside of this workspace, are considered,
        #     ...that have label names that are declared to be managed by the build, are considered
        #     ...(or thus,) that are buildable by the ide, are not considered,
        # - libs only discovered via test mode are scope="TEST"
        # - libs that are present in both test mode and non-test mode, are considered compile dependencies (so, no scope attribute)

        expected_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name="NewModuleRootManager">
    <content>
      <existing />
    </content>
    <orderEntry type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/relative/path/off/the/execroot/c-show-up-in-non-test-mode-only.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
    <orderEntry type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/relative/path/off/the/execroot/d-show-up-in-both-modes.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
    <orderEntry type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/relative/path/off/the/execroot/e-was-generated-by-the-build.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
    <orderEntry scope="TEST" type="module-library">
      <library>
        <CLASSES>
          <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/some/relative/path/off/the/execroot/b-show-up-in-test-mode-only.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </orderEntry>
  </component>
</module>
""".strip() + "\n"
        self.assertEqual(expected_content, convert_xml_element_to_pretty_printed_xml_string(element))

        def match_parse_failure():
            JarDependencyMatcher('xyxyx')

        self.assertRaises(JarDependencyMatchFormatException, match_parse_failure)

    def test_determine_module_deps(self):
        # basic match/no-match
        self.assertEqual({"a_package": ["b_package", "e_package"]},
                         determine_package_deps([{"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "b_package"},
                                                 {"bazel_package": "c_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "NOMATCH",
                                                 "depends_on_bazel_package": "d_package"},
                                                 {"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "e_package"}],
                                                [ModuleDependencyMatcher('{"attr":"deps"}')],
                                                "the_root_package"))

        # filesystem-like wildcarding
        self.assertEqual({"a_package": ["b_package", "e_package"]},
                         determine_package_deps([{"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "b_package"},
                                                 {"bazel_package": "c_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "NOMATCH",
                                                 "depends_on_bazel_package": "d_package"},
                                                 {"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "e_package"}],
                                                [ModuleDependencyMatcher('{"attr":"de*"}')],
                                                "the_root_package"))

        # match on multiple attrs
        self.assertEqual({"a_package": ["b_package"],
                          "c_package": ["b_package"]},
                         determine_package_deps([{"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "b_package"},
                                                 {"bazel_package": "c_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "b_package"},
                                                 {"bazel_package": "a_package",
                                                 "label_name": "x_label_name",
                                                 "attr_name": "deps",
                                                 "depends_on_bazel_package": "e_package"}],
                                                [ModuleDependencyMatcher('{"to_package":"b_package", "attr":"deps"}')],
                                                "the_root_package"))

        def match_parse_failure():
            ModuleDependencyMatcher('xyxyx')
        self.assertRaises(ModuleDependencyMatchFormatException, match_parse_failure)

    def test_convert_bazel_package_deps_to_intellij_module_deps(self):
        defined_modules = [
            {"bazel_package": "z/foo"},
            {"bazel_package": "z/bar"},
            {"bazel_package": "z/a"},
            {"bazel_package": "z/bbb", "module_name_override": "b"},
            {"bazel_package": "z/c"},
            {"bazel_package": "d"},
            {"bazel_package": ""},
        ]

        self.assertEqual({
            "z/foo": ["a", "b"],
            "z/bar": ["c", "d"]
        }, convert_bazel_package_deps_to_intellij_module_deps({
            "z/foo": ["z/a", "z/bbb", "z/not_available_in_list"],
            "z/bar": ["z/c", "d"]
        }, defined_modules, "yyy"))

        # root package
        self.assertEqual({
            "": ["a"]
        }, convert_bazel_package_deps_to_intellij_module_deps({
            "": ["z/a"],
        }, defined_modules, "yyy"))

    def test_insert_bazel_package_dep_into_iml_element_as_module_dep(self):
        iml_base_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
""".strip() + "\n"

        expected_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry forTests="false" type="sourceFolder" />
    <orderEntry module-name="bar" type="module" />
    <orderEntry module-name="zzz" type="module" />
  </component>
</module>
""".strip() + "\n"

        element1 = parse_xml(iml_base_content)
        insert_bazel_package_dep_into_iml_element_as_module_dep(element1, "bar")
        insert_bazel_package_dep_into_iml_element_as_module_dep(element1, "zzz")

        self.assertEqual(expected_content, convert_xml_element_to_pretty_printed_xml_string(element1))

    def test_add_bazel_package_deps(self):
        iml_base_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
""".strip() + "\n"

        expected_content = """
<?xml version='1.0' encoding='UTF-8'?>
<module>
  <component name=\"NewModuleRootManager\">
    <content>
      <existing />
    </content>
    <orderEntry forTests="false" type="sourceFolder" />
    <orderEntry module-name="bar" type="module" />
    <orderEntry module-name="zzz" type="module" />
  </component>
</module>
""".strip() + "\n"

        element = parse_xml(iml_base_content)
        insert_all_module_deps(
            {"foo": IntellijModuleComposer({}, element)},
            {"foo": ["bar", "zzz"]})

        self.assertEqual(expected_content, convert_xml_element_to_pretty_printed_xml_string(element))

    def test_make_modules_xml(self):
        expected_content = """
<?xml version='1.0' encoding='UTF-8'?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/bar.iml" filepath="$PROJECT_DIR$/bar.iml"/>
      <module fileurl="file://$PROJECT_DIR$/foo/subfoo.iml" filepath="$PROJECT_DIR$/foo/subfoo.iml"/>
    </modules>
  </component>
</project>""".strip() + "\n"

        self.assertEqual(
            expected_content,
            make_modules_xml("root/subroot", ["root/subroot/foo/subfoo.iml", "root/subroot/bar.iml"]))

    def test_xmls_to_sha1s(self):
        self.assertEqual(
            {"bar.iml": "4d18aab61d7c4874a70ff4750f1e066291fff399",
             "foo.iml": "13e190a16a3937a346f2f2104210a9d0af775cec"},
            xmls_to_sha1s({"bar.iml": "<bar/>", "foo.iml": "<foo/>"}))

    def test_intellij_files_archive(self):
        expected_archive_contents = """
sha1bar bar.iml
sha1foo foo.iml
__SHA1_DIVIDER__
bar.iml
<bar />
__FILE_DIVIDER__
foo.iml
<foo />
__SYMLINK_DIVIDER__
some_execroot_file|symlink_under_project_file
""".strip()
        self.assertEqual(
            expected_archive_contents,
            make_intellij_files_archive(
                {"sha1foo": "foo.iml",
                 "sha1bar": "bar.iml"},
                {"foo.iml": "<foo />", "bar.iml": "<bar />"},
                {"some_execroot_file":"symlink_under_project_file"}))


# TODO: test that workspace xml fragment paths are relative to .idea

if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(RulesIntellijGenerateTest))

    # python unit test main's must end with this or the test will exit with status code 0,
    # and thus it will not fail the bazel test run if there's a test failure.
    return_value = not unittest.TextTestRunner(verbosity=2).run(suite).wasSuccessful()
    sys.exit(return_value)
