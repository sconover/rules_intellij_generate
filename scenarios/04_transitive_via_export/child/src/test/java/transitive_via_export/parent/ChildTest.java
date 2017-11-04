package transitive_via_export.parent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChildTest {
  @Test
  public void print_hello_three_times() {
    assertTrue(new Child().canIUseGuava());
  }
}