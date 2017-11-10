package intellij_generate.scenarios;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static intellij_generate.scenarios.TestUtil.loadBazelGeneratedFile;
import static intellij_generate.scenarios.TestUtil.removeWorkingDirectory;
import static intellij_generate.scenarios.TestUtil.xpathList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S06ProtobufMessagesTest {
  private static String usageImlContent;

  @BeforeAll
  public static void before_all() {
    usageImlContent = loadBazelGeneratedFile("06_protobuf_messages/usage/iml.iml");
  }

  @Test
  public void libraries() {
    // use of bazel java proto tasks should result in inclusion of the google protobuf library,
    // as well as jars containing the compiled code-gen'd classes based on the specified proto definitions.
    assertEquals(asList(
      "bazel-out/darwin_x86_64-fastbuild/bin/06_protobuf_messages/plain_email/libproto-speed.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/external/com_google_protobuf_java/libprotobuf_java.jar!/",
      "bazel-out/darwin_x86_64-fastbuild/bin/06_protobuf_messages/html_email/libproto-speed.jar!/"),
      removeWorkingDirectory(
        xpathList(usageImlContent, "/module/component/orderEntry[@type='module-library' and not(@scope)]/library/CLASSES/root/@url")));

    assertEquals(asList(
      "external/org_junit_jupiter_junit_jupiter_api/jar/junit-jupiter-api-5.0.1.jar!/",
      "external/org_opentest4j_opentest4j/jar/opentest4j-1.0.0.jar!/"),
      removeWorkingDirectory(
        xpathList(usageImlContent, "/module/component/orderEntry[@type='module-library' and @scope='TEST']/library/CLASSES/root/@url")));
  }
}
