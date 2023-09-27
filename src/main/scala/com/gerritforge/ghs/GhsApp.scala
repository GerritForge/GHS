package com.gerritforge.ghs

import com.gerritforge.ghs.metrics.{CustomScheduler, Metric}
import kamon.Kamon
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory

import java.util.Properties
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object GhsApp extends App {
  Kamon.init()

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler: Scheduler = new StdSchedulerFactory(properties).getScheduler
  val customScheduler      = new CustomScheduler(scheduler)

  scheduler.start()

  val config = Config.apply("collection.conf", "tasks.conf")
  config.metricsCollection.projects
    .foreach { project =>
      customScheduler.schedule(config.metricsCollection.gitSiteBasePath, project, Metric.JGit)
      customScheduler.schedule(config.metricsCollection.gitSiteBasePath, project, Metric.FileSystem)
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
