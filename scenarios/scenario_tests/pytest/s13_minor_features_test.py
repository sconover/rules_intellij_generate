import unittest
from scenario_test_support import *


class S13MinorFeaturesTest(unittest.TestCase):
    archive = load_archive("13_minor_features/intellij_files")

    iml_content = archive["13_minor_features/13_mymodulename.iml"]

    def test_source_folders(self):
        self.assertEqual([
            "file://$MODULE_DIR$/out/production/generated",
            "file://$MODULE_DIR$/out/test/generated_tests",
            "file://$MODULE_DIR$/src",
            "file://$MODULE_DIR$/test",
            "file://$MODULE_DIR$/${MY_BICYCLE_COLOR}",
            "file://$MODULE_DIR$/${SOME_FILE}",
            "file://${BAZEL_PACKAGE_GENFILES}",
        ], xpath_attribute_list(self.iml_content, "./component/content/sourceFolder", "url"))

    def test_provide_bash_exports_for_custom_substitutions(self):
        script_content = read_file(generated_file_path("13_minor_features/install_intellij_files_script"))
        custom_vars_in_python = script_content.split("# BEFORE_CUSTOM_VARS")[1].split("# AFTER_CUSTOM_VARS")[0]

        self.assertEqual(
                "'MY_BICYCLE_COLOR':'red',\n" +
                "'SOME_FILE':'13_minor_features/some_file.txt',",
                custom_vars_in_python.strip())

        self.assertContains(script_content,
                            'for line in get_subcommand_output(["my-bazel-script", "info"]).splitlines()')
