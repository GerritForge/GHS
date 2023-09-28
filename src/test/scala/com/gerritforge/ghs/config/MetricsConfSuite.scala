package com.gerritforge.ghs.config

import com.gerritforge.ghs.config.MetricsConf.Project
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

import java.io.File
import java.nio.file.Files
import scala.concurrent.duration._

class MetricsConfSuite extends munit.FunSuite {
  test("pureConfig should read metrics config") {
    val project1Folder = Files.createTempDirectory("project1").toFile.getAbsolutePath
    val project2Folder = Files.createTempDirectory("project2").toFile.getAbsolutePath
    val collectionConfiguration =
      s"""
        |projects = [
        |    {
        |        path = $project1Folder
        |        frequency = 1h
        |    }
        |    {
        |        path = $project2Folder
        |        frequency = 1m
        |    }
        |]
        |""".stripMargin

    val result = ConfigSource
      .fromConfig(ConfigFactory.parseString(collectionConfiguration))
      .load[MetricsConf]

    val expected = MetricsConf(
      List(
        Project(new File(project1Folder), 1.hour),
        Project(new File(project2Folder), 1.minute)
      )
    )
    assertEquals(result, Right(expected))
  }

  test("pureConfig should fails when project is not a directory") {
    val project1AsFile = Files.createTempFile("prefix", ".txt").toFile.getAbsolutePath
    val collectionConfiguration =
      s"""
         |projects = [
         |    {
         |        path = ${project1AsFile}
         |        frequency = 1h
         |    }
         |]
         |""".stripMargin

    val exception = intercept[IllegalArgumentException](
      ConfigSource
        .fromConfig(ConfigFactory.parseString(collectionConfiguration))
        .load[MetricsConf]
    )

    assertEquals(
      exception.getMessage,
      s"requirement failed: ${project1AsFile} should be a directory"
    )
  }
}
