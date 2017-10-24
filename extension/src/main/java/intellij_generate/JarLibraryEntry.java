package intellij_generate;

import java.util.ArrayList;
import java.util.List;

import static intellij_generate.Util.fileJoin;
import static intellij_generate.Util.readFile;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Represents an entry from a manifest file provided by the bazel build to the iml-generator.
 * The manifest file consists of two columns: name and path-on-disk-to-the-jar-file.
 */
public class JarLibraryEntry {
  static List<JarLibraryEntry> loadLibraryEntriesFromManifestFile(String execRootPath, String librariesManifestPath) {
    String fileContent = readFile(librariesManifestPath);
    if (fileContent.trim().length() == 0) {
      return new ArrayList<>();
    } else {
      return asList(fileContent.split("\n")).stream()
        .map(line -> {
          String[] parts = line.split(" ");
          String name = parts[0];
          String path = parts[1];
          return new JarLibraryEntry(name, fileJoin(execRootPath, path));
        })
        .collect(toList());
    }
  }

  public final String name;
  public final String path;

  public JarLibraryEntry(String name, String path) {
    this.name = name;
    this.path = path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JarLibraryEntry that = (JarLibraryEntry) o;

    return path.equals(that.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return format("JarLibraryEntry[%s,%s]", name, path);
  }
}
