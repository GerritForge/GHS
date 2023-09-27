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

  val metricsCollection = getClass.getResource("/collection.conf").getPath
  val tasks             = getClass.getResource("/tasks.conf").getPath
  val config            = Config.apply(metricsCollection, tasks)
  config.metricsCollection.projects
    .foreach { project =>
      scheduler.scheduleJob(
        SchedulerUtilities
          .JobDetail(MetricsData.apply(config.metricsCollection.gitSiteBasePath, project), new CollectionJob.JGit()),
        SchedulerUtilities.Trigger("jgit", project)
      )
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
