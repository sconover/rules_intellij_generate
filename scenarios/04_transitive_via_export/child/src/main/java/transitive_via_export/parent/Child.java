package transitive_via_export.parent;

class Child {
  public boolean canIUseGuava() {
    // demonstrates use of guava,
    // only accessible because the grandparent exports the library

    try {
      Class.forName("com.google.common.base.Strings");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}