package com.gerritforge.ghs

import com.gerritforge.ghs.Config.{MetricsCollection, Tasks}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.File
import scala.concurrent.duration._

class ConfigSpec extends AnyFlatSpec with Matchers {
  "PureConfig" should "read a valid config" in {
    val expectedMetrics = MetricsCollection(
      gitSiteBasePath = new File("/directory1/directory2"),
      projects = List(MetricsCollection.Project("project1", 1.hour), MetricsCollection.Project("project2", 1.minute))
    )
    val expectedTasks =
      Tasks(List(Tasks.Project("project1", "rm", "-fr"), Tasks.Project("project2", "jgit gc", "--prune=now")))

    val metricsCollection = getClass.getResource("/collection.conf").getPath
    val tasks             = getClass.getResource("/tasks.conf").getPath
    Config(metricsCollection, tasks) shouldBe Config(expectedMetrics, expectedTasks)
  }
}
