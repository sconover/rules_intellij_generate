package intellij_generate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Util {
  static void writeLinesToFileAsUTF8(String path, List<String> lines) {
    new File(new File(path).getParent()).mkdirs();
    try {
      System.out.println(
        format(">> IML CONTENT START %s", path) + "\n" +
          lines.stream().collect(Collectors.joining("\n")) + "\n" +
          format("<< IML CONTENT FINISH %s", path));
      Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static String readFile(String pathStr) {
    try {
      checkState(pathStr != null, "expected file path to be non-null");
      Path pathObject = Paths.get(pathStr);
      checkState(pathObject.toFile().exists(), format("expected file path to exist %s", pathStr));
      return new String(Files.readAllBytes(pathObject));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  static String fileJoin(String aPath, String bPath) {
    return new File(aPath, bPath).getPath();
  }

  private static void checkState(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }
}
