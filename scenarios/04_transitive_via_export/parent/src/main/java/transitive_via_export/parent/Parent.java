package transitive_via_export.parent;

import com.google.common.base.Strings;

class Parent {
  public String hello4times() {
    // demonstrates use of guava,
    // only accessible because the grandparent exports the library

    try {
      Class.forName("com.google.common.base.Strings");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    return Strings.repeat("hello", 4);
  }
}