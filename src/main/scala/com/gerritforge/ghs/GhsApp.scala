package com.gerritforge.ghs

import com.gerritforge.ghs.metrics.{CollectionJob, MetricsData, SchedulerUtilities}
import kamon.Kamon
import org.quartz.impl.StdSchedulerFactory

import java.util.Properties
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object GhsApp extends App {
  Kamon.init()

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()
  val config = Config.apply("collection.conf", "tasks.conf")
  config.metricsCollection.projects
    .foreach { project =>
      scheduler.scheduleJob(
        SchedulerUtilities
          .JobDetail(MetricsData.jgit(config.metricsCollection.gitSiteBasePath, project), new CollectionJob.JGit()),
        SchedulerUtilities.Trigger("jgit", project)
      )
    }

  config.metricsCollection.projects
    .foreach { project =>
      scheduler.scheduleJob(
        SchedulerUtilities
          .JobDetail(MetricsData.fs(config.metricsCollection.gitSiteBasePath, project), new CollectionJob.FS()),
        SchedulerUtilities.Trigger("fs", project)
      )
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
