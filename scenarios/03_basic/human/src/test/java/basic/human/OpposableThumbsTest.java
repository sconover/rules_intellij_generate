package basic.human;

import basic.mammal.WarmBlood;
import basic.primate.ColorVision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpposableThumbsTest {
  @Test
  public void ancestry() {
    assertTrue(ColorVision.class.isAssignableFrom(OpposableThumbs.class));
    assertTrue(WarmBlood.class.isAssignableFrom(OpposableThumbs.class));
  }
}