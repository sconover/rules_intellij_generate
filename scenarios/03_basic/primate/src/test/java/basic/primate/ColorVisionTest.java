package basic.primate;

import basic.mammal.WarmBlood;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ColorVisionTest {
  @Test
  public void ancestry() {
    assertTrue(WarmBlood.class.isAssignableFrom(ColorVision.class));
  }
}