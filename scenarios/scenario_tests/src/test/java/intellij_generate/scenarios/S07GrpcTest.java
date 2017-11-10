package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S07GrpcTest {
  private static String fortuneGrpcImlContent;

  @BeforeAll
  public static void before_all() {
    fortuneGrpcImlContent = loadBazelGeneratedFile("07_grpc/iml.iml");
  }

  @Test
  public void libraries() {
    // might be going overboard, making a really brittle test. we'll see.
    assertEquals(asList(
      "bazel-out/darwin_x86_64-fastbuild/bin/07_grpc/libgrpc.jar!/",
      "external/com_google_code_findbugs_jsr305/jar/jsr305-3.0.0.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/07_grpc/libproto-speed.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/com_google_protobuf_java/libprotobuf_java.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/core/libcore.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/context/libcontext.jar!/",
      "external/com_google_errorprone_error_prone_annotations/jar/error_prone_annotations-2.0.19.jar!/",
      "external/com_google_guava/jar/guava-19.0.jar!/",
      "external/com_google_instrumentation_api/jar/instrumentation-api-0.4.3.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/stub/libstub.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/protobuf/libprotobuf.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/protobuf-lite/libprotobuf_lite.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/grpc_java/core/libinternal.jar!/",
      "external/com_google_api_grpc_google_common_protos/jar/proto-google-common-protos-0.1.9.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/com_google_protobuf/libprotobuf_java.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/com_google_protobuf/libprotobuf_java_util.jar!/",
      "external/com_google_code_gson/jar/gson-2.7.jar!/"),
      removeWorkingDirectory(
        xpathList(fortuneGrpcImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url")));

    assertEquals(asList(
      "external/org_junit_jupiter_junit_jupiter_api/jar/junit-jupiter-api-5.0.1.jar!/",
      "external/org_opentest4j_opentest4j/jar/opentest4j-1.0.0.jar!/"),
      removeWorkingDirectory(
        xpathList(fortuneGrpcImlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url")));
  }
}
