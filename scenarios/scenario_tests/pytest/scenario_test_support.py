import xml.etree.ElementTree as ET
import os

dir_path = os.path.dirname(os.path.realpath(__file__))


def is_bazel_run():
    return "TEST_WORKSPACE" in os.environ


def read_file(path):
    f = open(path, "r")
    content = f.read()
    f.close()
    return content


def parse_xml(xml_str):
    return ET.fromstring(xml_str)


def xpath_list(xml_str, xpath):
    return parse_xml(xml_str).findall(xpath)


# unfortunately ElementTree doesn't handle returning xpath attribute values, this is a workaround.
def xpath_attribute_list(xml_str, xpath, attribute_name):
    return map(lambda e: e.get(attribute_name), parse_xml(xml_str).findall(xpath))


def generated_file_path(relative_path):
    return relative_path \
        if is_bazel_run() \
        else os.path.join(dir_path, "../../bazel-bin/%s" % relative_path)

def load_archive(intellij_files_archive_path):
    intellij_files_archive_path = generated_file_path(intellij_files_archive_path)

    entries = read_file(intellij_files_archive_path).split("__SHA1_DIVIDER__\n", 1)[1].split("\n__FILE_DIVIDER__\n")

    relative_path_to_content = {}
    for entry in entries:
        parts = entry.split("\n", 1)
        relative_path_to_content[parts[0]] = parts[1]

    return relative_path_to_content

def find_all_plain_jar_libraries(iml_content):
    return map(lambda e: e.find("./library/CLASSES/root").get("url"),
        filter(lambda e: e.get("type") == "module-library" and "scope" not in e.keys(),
               parse_xml(iml_content).findall("./component/orderEntry")))

def find_all_test_jar_libraries(iml_content):
    return map(lambda e: e.find("./library/CLASSES/root").get("url"),
        filter(lambda e: e.get("type") == "module-library" and e.get("scope") == "TEST",
               parse_xml(iml_content).findall("./component/orderEntry")))

def junit5_jars():
    return [
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_apiguardian_apiguardian_api/jar/apiguardian-api-1.0.0.jar!/",
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_junit_jupiter_junit_jupiter_api/jar/junit-jupiter-api-5.0.1.jar!/",
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_junit_platform_junit_platform_commons/jar/junit-platform-commons-1.0.1.jar!/",
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_junit_platform_junit_platform_engine/jar/junit-platform-engine-1.0.1.jar!/",
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_junit_platform_junit_platform_launcher/jar/junit-platform-launcher-1.0.1.jar!/",
        "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/org_opentest4j_opentest4j/jar/opentest4j-1.0.0.jar!/"]
