package kotlin_example.child

import kotlin_example.parent.Parent
import java.lang.String.format

class Child {
  fun sayHello(): String {
    return "hello"
  }

  fun sayHelloAndLa(): String {
    return sayHello() + Parent().la4times()
  }
}