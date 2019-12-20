import unittest

from scenario_test_support import *


class S06ProtobufMessagesTest(unittest.TestCase):
    archive = load_archive("06_protobuf_messages/intellij_files")

    usage_iml_content = archive["06_protobuf_messages/usage/usage.iml"]

    def test_libraries(self):
        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libdescriptor_proto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/06_protobuf_messages/html_email/libproto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/06_protobuf_messages/plain_email/libproto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libprotobuf_java.jar!/",
        ], find_all_plain_jar_libraries(self.usage_iml_content))

        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.usage_iml_content))
