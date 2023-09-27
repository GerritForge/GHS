package com.gerritforge.ghs

import kamon.Kamon

object GhsApp extends App {
  Kamon.init()
  while (true) {
    Thread.sleep(1000)
    Kamon.counter("hello.GHS").withoutTags().increment()
  }
}
