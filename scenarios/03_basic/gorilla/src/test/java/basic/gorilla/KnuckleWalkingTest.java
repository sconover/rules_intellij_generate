package basic.gorilla;

import basic.mammal.WarmBlood;
import basic.primate.ColorVision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KnuckleWalkingTest {
  @Test
  public void ancestry() {
    assertTrue(ColorVision.class.isAssignableFrom(KnuckleWalking.class));
    assertTrue(WarmBlood.class.isAssignableFrom(KnuckleWalking.class));
  }
}