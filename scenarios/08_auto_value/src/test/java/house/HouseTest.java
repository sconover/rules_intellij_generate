package house;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HouseTest {
  @Test
  public void build_it() {
    House house = House.builder().setColor("red").setFloors(3).build();
    assertEquals("red", house.color());
    assertEquals(3, house.floors());
  }
}
