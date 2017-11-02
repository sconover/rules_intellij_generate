package transitive_via_export.parent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParentTest {
  @Test
  public void print_hello_three_times() {
    assertEquals("hellohellohellohello", new Parent().hello4times());
  }
}