package intellij_generate.iml;

import java.util.ArrayList;
import java.util.List;

import static intellij_generate.common.Util.fileJoin;
import static intellij_generate.common.Util.readFile;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Represents an entry from a manifest file provided by the bazel build to the iml-generator.
 * The manifest file consists of three columns:
 * - name
 * - path-on-disk-to-the-jar-file
 * - scope (e.g. COMPILE, TEST)
 */
class JarDependencyEntry {
  enum Scope {
    COMPILE,
    TEST
  }

  static List<JarDependencyEntry> loadLibraryEntriesFromManifestFile(String execRootPath, String librariesManifestPath) {
    String fileContent = readFile(librariesManifestPath);
    if (fileContent.trim().length() == 0) {
      return new ArrayList<>();
    } else {
      return asList(fileContent.split("\n")).stream()
        .map(line -> {
          String[] parts = line.split(" ");
          String name = parts[0];
          String path = parts[1];
          String scope = parts[2];
          return new JarDependencyEntry(name, fileJoin(execRootPath, path), Scope.valueOf(scope));
        })
        .collect(toList());
    }
  }

  public final String name;
  public final String path;
  public final Scope scope;

  public JarDependencyEntry(String name, String path, Scope scope) {
    this.name = name;
    this.path = path;
    this.scope = scope;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JarDependencyEntry that = (JarDependencyEntry) o;

    return path.equals(that.path);
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public String toString() {
    return format("JarLibraryEntry[%s,%s,%s]", name, path, scope.name());
  }
}
