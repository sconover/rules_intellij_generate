package minor_features;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BTest {
  @Test
  public void the_number() {
    assertEquals(77, new B().theNumber());
  }
}
