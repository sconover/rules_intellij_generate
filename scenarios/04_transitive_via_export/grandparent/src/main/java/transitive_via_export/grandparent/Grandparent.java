package transitive_via_export.grandparent;

import com.google.common.base.Strings;

class Grandparent {
  public String hello3times() {
    // demonstrates use of guava

    try {
      Class.forName("com.google.common.base.Strings");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    return Strings.repeat("hello", 3);
  }
}