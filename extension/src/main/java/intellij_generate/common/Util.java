package intellij_generate.common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Util {
  public static void writeStringToFileAsUTF8(String path, String content) {
    new File(new File(path).getParent()).mkdirs();
    try {
      System.out.println(
        format(">> FILE CONTENT START %s", path) + "\n" +
          content + "\n" +
          format("<< FILE CONTENT FINISH %s", path));
      Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String readFile(String pathStr) {
    try {
      checkState(pathStr != null, "expected file path to be non-null");
      return new String(Files.readAllBytes(checkPathExists(Paths.get(pathStr))));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String fileJoin(String aPath, String bPath) {
    return new File(aPath, bPath).getPath();
  }

  public static void checkState(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }

  public static Path checkPathExists(Path path) {
    checkState(path.toFile().exists(), format("expected path to exist: %s", path));
    return path;
  }

  public static String getExecRootPathInAnExtremelyEvilWayDoNotReleaseBeforeCheckingWithBazelTeam() {
    // this is awful and cannot be released, but I have no idea how else to get the execroot from the bazel env...
    String pwd = System.getenv("PWD");
    List<String> pwdParts = asList(pwd.split("/"));
    // we're in the sandbox dir, which is two levels below the real execroot.
    String workspaceName = pwdParts.get(pwdParts.size() - 1);
    List<String> execRootParts = new ArrayList<>(pwdParts.subList(0, pwdParts.size() - 4));
    execRootParts.add("execroot");
    execRootParts.add(workspaceName);
    return execRootParts.stream().collect(Collectors.joining("/"));
  }
}
