"""
Transforms a json file containing data which describes the bazel build configuration,
into a simple archive of intellij configuration files. Of note, because this script
executes as part of a bazel build, it is subject to the rules of the sandbox,
so this code can't know the actual path to the execution root directory, where
jar dependencies and other artifacts reside. We can't break out of the Matrix.

(The role of the install script is to resolve this difficulty: this script is produced by
the build run, but the user runs it independently, so it may access all the information
available from "bazel info", and merge those values with the archive produced by this
script, and thus produce and place "final" intellij xml config files)

Organziation of this script roughly from low level (utility functions) to high level
(main, at the end). Functions are organized in rough order of when they're applied by
the main method. To understand what's being done, it's probably best to start at the
end and work backwards.
"""

import copy
import hashlib
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from fnmatch import fnmatch

# ===================
# UTILITY FUNCTIONS
# ===================

def read_file(path):
    f = open(path, "r")
    content = f.read()
    f.close()
    return content

def read_files(paths):
    path_to_content = {}
    for p in paths:
        path_to_content[p] = read_file(p)
    return path_to_content

def write_file(path, content):
    f = open(path, "w")
    f.write(content)
    f.close()

def check_state(condition, message):
    if not condition:
        raise Exception(message)

def parse_xml(xml_str):
    return ET.fromstring(xml_str)

# adapted with corrections from https://stackoverflow.com/a/4590052
def xml_indent(elem, level=0, is_last=True):
    inner_indent = "\n" + level * "  "
    outer_indent = "\n" + (level-1) * "  "
    if len(elem):
        if not elem.text or not elem.text.strip():
            elem.text = inner_indent + "  "
        if not elem.tail or not elem.tail.strip():
            elem.tail = inner_indent
        num_subelements = len(elem)
        for idx, subelem in enumerate(elem):
            xml_indent(subelem, level+1, is_last=idx==num_subelements-1)
        if not elem.tail or not elem.tail.strip():
            if is_last:
                elem.tail = outer_indent
            else:
                elem.tail = inner_indent
    else:
        if level and (not elem.tail or not elem.tail.strip()):
            if is_last:
                elem.tail = outer_indent
            else:
                elem.tail = inner_indent
    return elem

def convert_xml_element_to_pretty_printed_xml_string(element):
    return ET.tostring(xml_indent(element), encoding="UTF-8", method="xml")

def check_for_valid_declared_intellij_module(declared_intellij_module):
    check_state("bazel_package" in declared_intellij_module,
                "Not a valid declared_intellij_module: '%s'" % declared_intellij_module)
    check_state("iml_type" in declared_intellij_module,
                "Not a valid declared_intellij_module: '%s'" % declared_intellij_module)
    # check_state("module_name_override" in declared_intellij_module,
    #             "Not a valid declared_intellij_module: '%s'" % declared_intellij_module)


# ===================
# CORE BAZEL BUILD DATA -> INTELLIJ CONFIG TRANSFORMATION FUNCTIONS
# ===================

def module_name(declared_intellij_module, root_package):
    if "module_name_override" in declared_intellij_module and \
            declared_intellij_module["module_name_override"] != None and \
            declared_intellij_module["module_name_override"] != "":
        return declared_intellij_module["module_name_override"]
    elif declared_intellij_module["bazel_package"] == "":
        return root_package
    else:
        return os.path.basename(declared_intellij_module["bazel_package"])

class IntellijModuleComposer():
    """Of note, holds an xml element object, the initial state of which comes from cloning the contents of
    an iml type (from the user-specified iml types xml file), which forms the "base" of the ultimate
    intellij module iml file."""
    def __init__(self, declared_intellij_module, intellij_module_xml_element):
        self.declared_intellij_module = declared_intellij_module
        self.intellij_module_xml_element = intellij_module_xml_element

def make_bazel_package_iml_composers(iml_types_xml, declared_intellij_modules):
    """Given the contents of the user-specified iml types xml file,
    Plus the list of modules declared for this intellij project
    (each entry derived from an intellij_module target),
    create a mapping of bazel package name to "iml composer".
    And iml composer is based on a clone of the imtellij_module's
    specified iml type name, thus becoming the "base" xml of the
    ultimate iml file.
    This is the place where the iml type name is resolved to an iml
    base xml document.
    """
    for m in declared_intellij_modules:
        check_for_valid_declared_intellij_module(m)

    iml_type_to_element = {}
    for iml_type_element in parse_xml(iml_types_xml).findall("./iml-type"):
        iml_type_name = iml_type_element.attrib["name"]
        children = list(iml_type_element)
        check_state(len(children) == 1,
                    "iml type '%s' invalid, iml types must have single-node-rooted xml document" % iml_type_name)
        iml_type_to_element[iml_type_name] = children[0]

    bazel_package_to_composer = {}

    for m in declared_intellij_modules:
        check_state(m["iml_type"] in iml_type_to_element,
                    "No iml type defined for type='%s'" % m["iml_type"])

        template_element = iml_type_to_element[m["iml_type"]]
        intellij_module_element = copy.deepcopy(template_element)

        bazel_package_to_composer[m["bazel_package"]] = IntellijModuleComposer(m, intellij_module_element)
    return bazel_package_to_composer

def iml_path_from_declared_intellij_module(declared_intellij_module, root_package):
    bazel_package = declared_intellij_module["bazel_package"]
    the_module_name = module_name(declared_intellij_module, root_package)
    return "%s.iml" % the_module_name \
        if len(bazel_package) == 0 \
        else "%s/%s.iml" % (bazel_package, the_module_name)

def insert_jar_dep_into_iml_element_as_module_library(iml_element, relative_jar_path, is_test_mode):
    """Given an iml xml element, which contains the xml for the intellij module thus far,
    add the bazel-derived jar dependency to it, as an intellij module jar library."""
    scope_attribute = " scope=\"TEST\"" if is_test_mode else ""
    order_entry_element = \
        parse_xml("""
            <orderEntry type="module-library"%s>
              <library>
                <CLASSES>
                  <root url="jar://${BAZEL_INFO_EXECUTION_ROOT}/%s!/" />
                </CLASSES>
                <JAVADOC />
                <SOURCES />
              </library>
            </orderEntry>
        """ % (scope_attribute, relative_jar_path.strip()))

    component_element = iml_element.find("./component[@name='NewModuleRootManager']")
    check_state(component_element != None, "/module/component not found when adding jar library '%s'" % relative_jar_path)
    component_element.append(order_entry_element)

class JarDependencyMatchFormatException(Exception):
    def __init__(self, bad_module_dependency_matcher):
        message = "Jar dependency matcher '%s' failed to parse. This matcher must be a json document, and " \
                  "can have keys 'package' and 'label_name' (all are optional, and values " \
                  "default to '*' if unspecified)." % bad_module_dependency_matcher
        super(Exception, self).__init__(message)

class JarDependencyMatcher():
    def __init__(self, jar_dependency_match_json_str):
        try:
            m = json.loads(jar_dependency_match_json_str)
            self.package = m["package"] if "package" in m else "*"
            self.label_name = m["label_name"] if "label_name" in m else "*"
        except Exception:
            raise JarDependencyMatchFormatException(jar_dependency_match_json_str)

    def matches(self, jar_dep):
        return \
            (self.package == "*" or fnmatch(jar_dep["bazel_package"], self.package)) and \
            (self.label_name == "*" or fnmatch(jar_dep["label_name"], self.label_name))

def insert_all_jar_libraries(bazel_package_to_iml_composer,
                             managed_by_build_tool_label_matchlist,
                             test_lib_label_matchlist,
                             jar_deps):
    """Given all jar dependencies from a bazel build configuration,
    plus some jar dependency matching rules,
    plus all intellij module composers,
    create xml jar library entries in the matching module xml element, according to these rules:

    - jars that are not generated by the build - meaning, externally-provided - should be added
    - jars that are generated by the build, and "managed by bazel", according to the matching rules, should be added
    - jars from targets that match a test-lib matcher, should be added as "TEST"-type jar libraries,
      otherwise they are added with no type, meaning they become "compile"-type jar libraries
    """

    managed_by_build_tool_matchers = map(lambda m: JarDependencyMatcher(m), managed_by_build_tool_label_matchlist)
    def is_managed_by_build_tool(jar_dep):
        for m in managed_by_build_tool_matchers:
            if m.matches(jar_dep):
                return True
        return False

    test_lib_matchers = map(lambda m: JarDependencyMatcher(m), test_lib_label_matchlist)
    def is_test_lib(jar_dep):
        for m in test_lib_matchers:
            if m.matches(jar_dep):
                return True
        return False

    def jar_is_managed_by_bazel(j):
        return j["generated_by_build"] and is_managed_by_build_tool(j)

    jar_paths_managed_by_bazel = map(lambda j: j["relative_jar_path"], filter(jar_is_managed_by_bazel, jar_deps))

    def library_jar_dependency(j):
        if j["generated_by_build"] and j["relative_jar_path"] in jar_paths_managed_by_bazel:
            return True

        if j["owner_workspace_root"] != "":
            return True

        return False

    library_jar_deps = filter(library_jar_dependency, jar_deps)

    bazel_package_to_jar_deps = {}
    for j in library_jar_deps:
        if j["bazel_package"] not in bazel_package_to_jar_deps:
            bazel_package_to_jar_deps[j["bazel_package"]] = []

        bazel_package_to_jar_deps[j["bazel_package"]].append(j)

    for bazel_package in bazel_package_to_jar_deps:
        if bazel_package not in bazel_package_to_iml_composer:
            continue

        iml_element = bazel_package_to_iml_composer[bazel_package].intellij_module_xml_element
        jar_deps_for_package = bazel_package_to_jar_deps[bazel_package]

        def unique_and_sorted_jar_paths(jar_deps):
            jar_paths = map(lambda j: j["relative_jar_path"], jar_deps)
            jar_paths = set(jar_paths)
            return sorted(jar_paths, key=lambda p: os.path.basename(p))

        regular_mode_jar_paths = \
            unique_and_sorted_jar_paths(
                filter(lambda j: not is_test_lib(j), jar_deps_for_package))

        test_mode_jar_paths = \
            unique_and_sorted_jar_paths(
                filter(lambda j: is_test_lib(j), jar_deps_for_package))

        for p in regular_mode_jar_paths:
            if p in test_mode_jar_paths:
                test_mode_jar_paths.remove(p)

        for p in regular_mode_jar_paths:
            insert_jar_dep_into_iml_element_as_module_library(iml_element, p, is_test_mode=False)

        for p in test_mode_jar_paths:
            insert_jar_dep_into_iml_element_as_module_library(iml_element, p, is_test_mode=True)

class ModuleDependencyMatchFormatException(Exception):
    def __init__(self, bad_module_dependency_matcher):
        message = "Module dependency matcher '%s' failed to parse. This matcher must be a json document, and " \
                  "can have keys 'package', 'label_name', 'attr', and 'to_package' (all are optional, and values " \
                  "default to '*' if unspecified)." % bad_module_dependency_matcher
        super(Exception, self).__init__(message)

class ModuleDependencyMatcher():
    def __init__(self, module_dependency_match_json_str):
        try:
            m = json.loads(module_dependency_match_json_str)
            self.package = m["package"] if "package" in m else "*"
            self.label_name = m["label_name"] if "label_name" in m else "*"
            self.attr = m["attr"] if "attr" in m else "*"
            self.to_package = m["to_package"] if "to_package" in m else "*"
        except Exception:
            raise ModuleDependencyMatchFormatException(module_dependency_match_json_str)


    def matches(self, bazel_package_dep):
        return \
            (self.package == "*" or fnmatch(bazel_package_dep["bazel_package"], self.package)) and \
            (self.label_name == "*" or fnmatch(bazel_package_dep["label_name"], self.label_name)) and \
            (self.attr == "*" or fnmatch(bazel_package_dep["attr_name"], self.attr)) and \
            (self.to_package == "*" or fnmatch(bazel_package_dep["depends_on_bazel_package"], self.to_package))

class DuplicateModuleNameException(Exception):
    def __init__(self, duplicate_module_name, all_bazel_package_names):
        message = "Intellij module name '%s' found more than once. This means you have two or more " \
                       "bazel packages that end with this name, and this is a problem because module names in an " \
                       "Intellij project must be unique. You can either rename one of the directories, " \
                       "or (recommended) make use of the 'module_name_override' attribute on the " \
                       "intellij_module rule in BUILD file of one of these packages. " \
                       "All bazel packages: '%s'" % (duplicate_module_name, ",".join(all_bazel_package_names))
        super(Exception, self).__init__(message)

def determine_package_deps(bazel_package_deps, module_dependency_matchlist, root_package):
    """Given a full list of package dependencies, as determined by the skylark/aspect code in intellij_project.bzl,
    filter down to only the dependencies that should be considered,
    in establishing intellij module dependency relationships,
    using the matchlist.
    Produce a mapping of bazel package to other bazel packages it depends on."""
    def is_module_dependency_match(bazel_package_dep):
        for m in module_dependency_matchlist:
            if m.matches(bazel_package_dep):
                return True
        return False

    matched_package_deps = filter(lambda p: is_module_dependency_match(p), bazel_package_deps)

    package_to_depends_on_packages = {}
    for p in matched_package_deps:
        if p["bazel_package"] not in package_to_depends_on_packages:
            package_to_depends_on_packages[p["bazel_package"]] = []
        if (p["depends_on_bazel_package"] not in package_to_depends_on_packages[p["bazel_package"]]):
            package_to_depends_on_packages[p["bazel_package"]].append(p["depends_on_bazel_package"])

    return package_to_depends_on_packages

def check_unique_intellij_module_names(all_intellij_modules, root_package):
    """The default name for a module is the last part of the bazel package name.
    Ex: bazel a/b/c becomes intellij module "c", by default.
    (this default may be overridden, in an intellij_module target)
    If it turns out that two bazel packages result in the same module name,
    throw an error (see the error message for further explanation).

    Ex1: a/b/c and a/b/d produce intellij modules c and d, so there's no collision and no exception raised.
    Ex2: a/b/c and z/x/c produce intellij modules c and c, so there's a collision and and exception would be raised."""
    module_to_bazel_package = {}
    for m in all_intellij_modules:
        intellij_module_name = module_name(m, root_package)
        if intellij_module_name in module_to_bazel_package and \
                module_to_bazel_package[intellij_module_name] != m["bazel_package"]:
            raise DuplicateModuleNameException(intellij_module_name, sorted(module_to_bazel_package.values()))
        else:
            module_to_bazel_package[intellij_module_name] = m["bazel_package"]

def convert_bazel_package_deps_to_intellij_module_deps(package_to_depends_on_packages, all_modules, root_package):
    bazel_package_name_to_module_name = {}
    for m in all_modules:
        bazel_package_name_to_module_name[m["bazel_package"]] = module_name(m, root_package)

    bazel_package_to_depends_on_modules = {}
    for p in package_to_depends_on_packages:
        depends_on_packages = package_to_depends_on_packages[p]
        depends_on_packages = filter(lambda dp: dp in bazel_package_name_to_module_name, depends_on_packages)
        bazel_package_to_depends_on_modules[p] = \
            map(lambda dp: bazel_package_name_to_module_name[dp], depends_on_packages)

    return bazel_package_to_depends_on_modules

def insert_bazel_package_dep_into_iml_element_as_module_dep(iml_element, module_name):
    """Create a module dependency xml entry in an intellij module xml element, for the module name"""
    order_entry_element = parse_xml("<orderEntry module-name=\"%s\" type=\"module\"/>" % module_name)

    component_element = iml_element.find("./component[@name='NewModuleRootManager']")
    check_state(component_element != None, "/module/component not found when adding package dep '%s'" % module_name)
    component_element.append(order_entry_element)

def insert_all_module_deps(bazel_package_to_iml_composer, bazel_package_to_depends_on_modules):
    """Create all module dependency xml entries, for all modules"""

    for p in bazel_package_to_depends_on_modules:
        if p not in bazel_package_to_iml_composer:
            continue

        iml_element = bazel_package_to_iml_composer[p].intellij_module_xml_element

        depends_on_modules = bazel_package_to_depends_on_modules[p]
        depends_on_modules = sorted(depends_on_modules)
        for p in depends_on_modules:
            insert_bazel_package_dep_into_iml_element_as_module_dep(iml_element, p)

def make_modules_xml(project_path_from_workspace_root, iml_paths_from_workspace_root):
    """This script controls the full contents of .idea/modules.xml - the file
    intellij uses to discover what the modules are in an intellij project.
    Generate the modules.xml file contents."""
    result = """
<?xml version='1.0' encoding='UTF-8'?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
""".strip() + "\n"

    final_paths = []
    for p in iml_paths_from_workspace_root:
        final_path = re.sub(r"^%s/" % project_path_from_workspace_root, "", p)
        final_path = final_path[1:] if final_path.startswith("/") else final_path
        final_paths.append(final_path)

    for p in sorted(final_paths):
        result += "      <module fileurl=\"file://$PROJECT_DIR$/%s\" filepath=\"$PROJECT_DIR$/%s\"/>\n" % (p, p)

    result += """    </modules>
  </component>
</project>
"""

    return result

def composers_to_xmls(bazel_package_to_composer, root_package):
    """Convert intellij module "composer" xml element objects into pretty-printed xml strings"""
    iml_path_to_content = {}

    for bazel_package in bazel_package_to_composer:
        doc = bazel_package_to_composer[bazel_package]

        iml_path = iml_path_from_declared_intellij_module(doc.declared_intellij_module, root_package)
        iml_path_to_content[iml_path] = convert_xml_element_to_pretty_printed_xml_string(
            doc.intellij_module_xml_element)

    return iml_path_to_content

def xmls_to_sha1s(iml_path_to_content):
    """Calculate the sha1 hex digest of each xml document"""
    iml_path_to_sha1 = {}
    for iml_path in iml_path_to_content:
        iml_path_to_sha1[iml_path] = hashlib.sha1(iml_path_to_content[iml_path]).hexdigest()
    return iml_path_to_sha1

def file_path_under_dot_idea_directory(relative_path, root_bazel_package):
    return ".idea/%s" % relative_path if root_bazel_package == "" \
        else "%s/.idea/%s" % (root_bazel_package, relative_path)

def load_root_files(root_file_paths, root_ignore_prefix, root_bazel_package):
    """Load the contents of all source files specified as intellij project-level files,
    that will ultimately be placed somewhere under the .idea directory.
    The "root ignore prefix" will be stripped off the front of each of these
    paths."""
    root_file_path_to_content = read_files(root_file_paths)
    relative_root_path_to_content = {}
    for p in root_file_path_to_content:
        final_path = re.sub(r"^%s/" % root_ignore_prefix, "", p)
        final_path = file_path_under_dot_idea_directory(final_path, root_bazel_package)
        relative_root_path_to_content[final_path] = root_file_path_to_content[p]
    return relative_root_path_to_content

def load_workspace_fragments(workspace_xml_fragment_paths, root_bazel_package):
    """Load the contents of source files that are intended to be "component" fragments in .idea/workspace.xml"""
    workspace_fragment_file_to_content = read_files(workspace_xml_fragment_paths)
    relative_path_to_content = {}
    for f in workspace_fragment_file_to_content:
        relative_path = os.path.basename(f)
        relative_path = file_path_under_dot_idea_directory(relative_path, root_bazel_package)
        relative_path_to_content[relative_path] = workspace_fragment_file_to_content[f]
    return relative_path_to_content

def make_intellij_files_archive(iml_path_to_sha1, relative_path_to_xml_content, symlinks):
    """Final assembly of the intellij archives file, which contains all intellij files produced by this script,
    (relative path and content), concatenated together, with sha1's of each file at the top of the archive."""

    sorted_iml_path_to_sha1 = sorted(iml_path_to_sha1.items(), key=lambda t: t[0])
    iml_sha1_entries = map(lambda t: " ".join(t), sorted_iml_path_to_sha1)

    sorted_iml_path_to_content = sorted(relative_path_to_xml_content.items(), key=lambda t: t[0])
    iml_content_entries = map(lambda t: "%s\n%s" % (t[0], t[1]), sorted_iml_path_to_content)

    symlinks_entries = map(lambda k: "%s|%s" % (k, symlinks[k]), sorted(symlinks.keys()))

    return "\n".join(iml_sha1_entries) + "\n__SHA1_DIVIDER__\n" + \
        "\n__FILE_DIVIDER__\n".join(iml_content_entries) + \
        "\n__SYMLINK_DIVIDER__\n" + "\n".join(symlinks_entries)

def process_main(project_data_json_path, intellij_files_archive_output_path):
    project_data = json.loads(read_file(project_data_json_path))

    check_unique_intellij_module_names(project_data["modules"], project_data["root_bazel_package"])

    bazel_package_to_iml_composer = \
        make_bazel_package_iml_composers(
            read_file(project_data["iml_types_path"]),
            project_data["modules"])

    insert_all_jar_libraries(
        bazel_package_to_iml_composer,
        project_data["build_managed_label_matchlist"],
        project_data["test_lib_label_matchlist"],
        project_data["jar_deps"])

    module_dependency_matchlist = map(lambda x: ModuleDependencyMatcher(x), project_data["module_dependency_matchlist"])
    insert_all_module_deps(
        bazel_package_to_iml_composer,
        convert_bazel_package_deps_to_intellij_module_deps(
            determine_package_deps(
                project_data["bazel_package_deps"],
                module_dependency_matchlist,
                project_data["root_bazel_package"]),
            project_data["modules"],
            project_data["root_bazel_package"]))

    iml_path_to_xml_content = \
        composers_to_xmls(bazel_package_to_iml_composer, project_data["root_bazel_package"])

    root_file_path_to_content = \
        load_root_files(
            project_data["project_root_files_paths"],
            project_data["project_root_files_path_ignore_prefix"],
            project_data["root_bazel_package"])

    workspace_fragment_path_to_content = \
        load_workspace_fragments(
            project_data["workspace_xml_fragment_paths"],
            project_data["root_bazel_package"])

    archive_content = {}
    archive_content.update(iml_path_to_xml_content)
    archive_content.update({
        file_path_under_dot_idea_directory("modules.xml", project_data["root_bazel_package"]):
        make_modules_xml(project_data["root_bazel_package"], iml_path_to_xml_content.keys())})
    archive_content.update(root_file_path_to_content)
    archive_content.update(workspace_fragment_path_to_content)

    write_file(
        intellij_files_archive_output_path,
        make_intellij_files_archive(
            xmls_to_sha1s(archive_content),
            archive_content,
            project_data["symlinks"]))

# ...this is invoked from within intellij_project.bzl
if __name__ == '__main__':
    check_state(len(sys.argv) == 3, "Two args expected: the path to the project data json file, " +
                "and the intellij files output path.")
    project_data_json_path = sys.argv[1]
    check_state(os.path.isfile(project_data_json_path),
                "project data json file does not exist: '%s'" % project_data_json_path)
    intellij_files_archive_output_path = sys.argv[2]
    process_main(project_data_json_path, intellij_files_archive_output_path)
