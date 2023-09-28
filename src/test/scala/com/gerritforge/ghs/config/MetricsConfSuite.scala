package com.gerritforge.ghs.config

import com.gerritforge.ghs.config.MetricsConf.Project
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig._
import pureconfig.generic.auto._

import java.io.File
import scala.concurrent.duration._

class MetricsConfSuite extends AnyFlatSpec with Matchers {
  "MetricsConf" should "read metrics config" in {
    val result = ConfigSource.resources("collection.conf").load[MetricsConf]
    val expected = MetricsConf(
      gerritSite = new File("/directory1/directory2"),
      projects = List(
        Project(new File("project1"), 1.hour),
        Project(new File("project2"), 1.minute)
      )
    )
    result shouldBe Right(expected)
  }
}
