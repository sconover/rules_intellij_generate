package intellij_generate.iml;

import java.util.ArrayList;
import java.util.List;

import static intellij_generate.common.Util.readFile;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Represents an entry from a manifest file provided by the bazel build to the iml-generator.
 * The manifest file consists of two columns:
 * - name
 * - scope (e.g. COMPILE, TEST)
 */
public class ModuleDependencyEntry {
  enum Scope {
    COMPILE,
    // TEST - right now, it's just compile
  }

  static List<ModuleDependencyEntry> loadModuleEntriesFromManifestFile(String modulesManifestPath) {
    String fileContent = readFile(modulesManifestPath);
    if (fileContent.trim().length() == 0) {
      return new ArrayList<>();
    } else {
      return asList(fileContent.split("\n")).stream()
        .map(line -> {
          String[] parts = line.split(" ");
          String name = parts[0];
          String scope = parts[1];
          return new ModuleDependencyEntry(name, Scope.valueOf(scope));
        })
        .collect(toList());
    }
  }

  public final String name;
  public final Scope scope;

  public ModuleDependencyEntry(String name, Scope scope) {
    this.name = name;
    this.scope = scope;
  }

  @Override
  public String toString() {
    return format("ModuleDependencyEntry[%s,%s]", name, scope.name());
  }
}
