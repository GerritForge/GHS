package com.gerritforge.ghs.config

import com.gerritforge.ghs.config.MetricsConf.Project
import pureconfig._
import pureconfig.generic.auto._

import java.io.File
import scala.concurrent.duration._

class MetricsConfSuite extends munit.FunSuite {
  test("pureConfig should read metrics config") {
    val result = ConfigSource.resources("collection.conf").load[MetricsConf]
    val expected = MetricsConf(
      List(
        Project(new File("project1"), 1.hour),
        Project(new File("project2"), 1.minute)
      )
    )
    assertEquals(result, Right(expected))
  }
}
