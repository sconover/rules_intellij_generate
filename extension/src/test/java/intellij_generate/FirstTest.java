package intellij_generate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstTest {
  @Test
  public void i_fail() {
    assertEquals(2, 1);
  }

  @Test
  public void i_pass() {
    assertEquals(1, 1);
  }
}
