package intellij_generate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

public class Util {
  static void writeStringToFileAsUTF8(String path, String content) {
    new File(new File(path).getParent()).mkdirs();
    try {
      System.out.println(
        format(">> IML CONTENT START %s", path) + "\n" +
          content + "\n" +
          format("<< IML CONTENT FINISH %s", path));
      Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static String readFile(String pathStr) {
    try {
      checkState(pathStr != null, "expected file path to be non-null");
      return new String(Files.readAllBytes(checkPathExists(Paths.get(pathStr))));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  static String fileJoin(String aPath, String bPath) {
    return new File(aPath, bPath).getPath();
  }

  static void checkState(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
    }
  }

  static Path checkPathExists(Path path) {
    checkState(path.toFile().exists(), format("expected path to exist: %s", path));
    return path;
  }
}
