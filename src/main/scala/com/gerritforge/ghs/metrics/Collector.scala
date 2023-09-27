package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}
import org.quartz.impl.StdSchedulerFactory

import java.io.File
import java.util.Properties
import scala.concurrent.Await
import scala.concurrent.duration._

object Collector extends App {
  Kamon.init()

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  // TODO this is for testing purposes
  val metricsProjects =
    Seq(
      MetricsConf.Project(new File("/Users/alvarovilaplana/src/pull-replication/.git"), 5.seconds)
    )

  metricsProjects
    .map(project => {
      val gc                                     = new GC(new FileRepository(project.path.getAbsolutePath))
      val gcF: Function[Unit, GC.RepoStatistics] = _ => gc.getStatistics
      CustomJobBuilder(
        context = MetricsContext.JGit,
        project = project,
        jobData = Map(
          MetricsContext.JGit.gcKey      -> gcF,
          MetricsContext.JGit.projectKey -> project,
          MetricsContext.JGit.metricKey  -> MetricsContext.JGit.metricName
        )
      )
    })
    .foreach { case (jobDetail, trigger) => scheduler.scheduleJob(jobDetail, trigger) }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
  Await.result(Kamon.stop(), 10.seconds)
}
