package kotlin_example.parent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParentTest {
  @Test
  fun `la 4 times`() {
    assertEquals("lalalala", Parent().la4times())
  }
}