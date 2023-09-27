package com.gerritforge.ghs

import com.gerritforge.ghs.config.MetricsConf
import com.gerritforge.ghs.metrics.{CollectionJob, ContextMetrics, SchedulerUtilities}
import kamon.Kamon
import org.quartz.impl.StdSchedulerFactory

import java.io.File
import java.util.Properties
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object GhsApp extends App {
  Kamon.init()

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  // TODO this is for testing purposes
  val metricsProjects =
    MetricsConf(
      gerritSite = new File("/Users/alvarovilaplana/src/gerrit-eclipse2/gerrithub"),
      projects = List(
        MetricsConf.Project(new File("alvaro.git"), 5.seconds),
        MetricsConf.Project(new File("asasa.git"), 5.seconds)
      )
    )

  metricsProjects.projects
    .foreach { project =>
      scheduler.scheduleJob(
        SchedulerUtilities
          .JobDetails[CollectionJob.JGit](
            ContextMetrics.JGit.`type`,
            project.name.getName,
            ContextMetrics.JGit.data(metricsProjects.gerritSite, project)
          ),
        SchedulerUtilities
          .Trigger(ContextMetrics.JGit.`type`, project)
      )
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
