package example

import kamon.Kamon

object Hello extends Greeting with App {
  println(greeting)
  Kamon.init()
  while (true) {
    Thread.sleep(1000)
    Kamon.counter("hello.GHS").withoutTags().increment()
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
