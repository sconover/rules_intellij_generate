package transitive_via_export.grandparent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GrandparentTest {
  @Test
  public void print_hello_three_times() {
    assertEquals("hellohellohello", new Grandparent().hello3times());
  }
}