package basic.dolphin;

import basic.mammal.WarmBlood;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EcholocationTest {
  @Test
  public void ancestry() {
    assertTrue(WarmBlood.class.isAssignableFrom(Echolocation.class));
  }
}