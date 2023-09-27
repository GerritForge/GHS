package com.gerritforge.ghs

import com.gerritforge.ghs.config.MetricsConf
import com.gerritforge.ghs.metrics.{CollectionJob, MetricsData, SchedulerUtilities}
import kamon.Kamon
import org.quartz.impl.StdSchedulerFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import java.util.Properties
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object GhsApp extends App {
  Kamon.init()

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  ConfigSource.resources("collection.conf").load[MetricsConf] match {
    case Left(_) => throw new IllegalArgumentException("Error loading configuration file")
    case Right(collectionConf) =>
      collectionConf.projects
        .foreach { project =>
          scheduler.scheduleJob(
            SchedulerUtilities
              .JobDetail(MetricsData.apply(collectionConf.gitSiteBasePath, project), new CollectionJob.JGit()),
            SchedulerUtilities.Trigger("jgit", project)
          )
        }
  }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
