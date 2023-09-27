package example

import org.scalatest.flatspec.AnyFlatSpec

class HelloSpec extends AnyFlatSpec {
  "Hello" should "say hello" in {
    assert(Hello.greeting == "hello")
  }
}
