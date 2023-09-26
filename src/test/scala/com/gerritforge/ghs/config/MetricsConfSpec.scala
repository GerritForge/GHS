package com.gerritforge.ghs.config

import com.gerritforge.ghs.config.MetricsConf
import com.gerritforge.ghs.config.MetricsConf.Project
import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration._

class MetricsConfSpec extends munit.FunSuite {
  test("pureConfig can read metrics config") {
    val result: Result[MetricsConf] = ConfigSource.resources("collection.conf").load
    val expected                    = MetricsConf(List(Project("project1", 1.hour), Project("project2", 1.minute)))
    assertEquals(result, Right(expected))
  }
}
