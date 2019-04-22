import unittest
import re
from scenario_test_support import *

class S07GrpcTest(unittest.TestCase):
    maxDiff = None

    archive = load_archive("07_grpc/intellij_files")

    fortune_grpc_iml_content = archive["07_grpc/07_grpc.iml"]

    def test_libraries(self):
        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_errorprone_error_prone_annotations/jar/error_prone_annotations-X.X.X.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_code_gson_gson/jar/gson-X.X.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-XX.X.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_code_findbugs_jsrXXX/jar/jsrXXX-X.X.X.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/io_grpc_grpc_java/context/libcontext.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/io_grpc_grpc_java/core/libcore.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libdescriptor_proto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/XX_grpc/libjava_grpc.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/XX_grpc/libproto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/io_grpc_grpc_java/protobuf-lite/libprotobuf-lite.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/io_grpc_grpc_java/protobuf/libprotobuf.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libprotobuf_java.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libprotobuf_java_util.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/io_grpc_grpc_java/stub/libstub.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_api_grpc_proto_google_common_protos/jar/proto-google-common-protos-X.X.X.jar!/",
        ], map(lambda x: re.sub(r"[0-9]", "X", x), find_all_plain_jar_libraries(self.fortune_grpc_iml_content)))

        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.fortune_grpc_iml_content))
