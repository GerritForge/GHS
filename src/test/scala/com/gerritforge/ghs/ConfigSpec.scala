package com.gerritforge.ghs

import com.gerritforge.ghs.Config.{Metrics, Tasks}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class ConfigSpec extends AnyFlatSpec with Matchers {
  "PureConfig" should "read a valid config" in {
    val expectedMetrics = Metrics(List(Metrics.Project("project1", 1.hour), Metrics.Project("project2", 1.minute)))
    val expectedTasks =
      Tasks(List(Tasks.Project("project1", "rm", "-fr"), Tasks.Project("project2", "jgit gc", "--prune=now")))

    val collection = getClass.getResource("/collection.conf").getPath
    val tasks      = getClass.getResource("/tasks.conf").getPath
    Config(collection.toString, tasks.toString) shouldBe Config(expectedMetrics, expectedTasks)
  }
}
