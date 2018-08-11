package kotlin_example.child

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ChildTest {
  @Test
  fun `say hello`() {
    assertEquals("hello", Child().sayHello())
  }

  @Test
  fun `say hello and la 4 times`() {
    assertEquals("hellolalalala", Child().sayHelloAndLa())
  }
}