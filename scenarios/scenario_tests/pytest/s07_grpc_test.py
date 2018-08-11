import unittest
from scenario_test_support import *

class S07GrpcTest(unittest.TestCase):

    archive = load_archive("07_grpc/intellij_files")

    fortune_grpc_iml_content = archive["07_grpc/07_grpc.iml"]

    def test_libraries(self):
        self.assertEqual([
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_errorprone_error_prone_annotations/jar/error_prone_annotations-2.0.19.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_code_gson_gson/jar/gson-2.7.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_guava_guava/jar/guava-19.0.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_instrumentation_instrumentation_api/jar/instrumentation-api-0.4.3.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_code_findbugs_jsr305/jar/jsr305-3.0.0.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/context/libcontext.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/core/libcore.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libdescriptor_proto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/core/libinternal.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/07_grpc/libjava_grpc.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/07_grpc/libproto-speed.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/protobuf/libprotobuf.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf/libprotobuf_java.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf_java/libprotobuf_java.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/com_google_protobuf_java/libprotobuf_java_util.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/protobuf-lite/libprotobuf_lite.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/bazel-out/darwin-fastbuild/bin/external/grpc_java/stub/libstub.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/io_opencensus_opencensus_api/jar/opencensus-api-0.7.0.jar!/",
            "jar://${BAZEL_INFO_EXECUTION_ROOT}/external/com_google_api_grpc_proto_google_common_protos/jar/proto-google-common-protos-0.1.9.jar!/",
        ], find_all_plain_jar_libraries(self.fortune_grpc_iml_content))

        self.assertEqual(junit5_jars(), find_all_test_jar_libraries(self.fortune_grpc_iml_content))
