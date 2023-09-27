package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}
import org.quartz.impl.StdSchedulerFactory

import java.io.File
import java.util.Properties
import scala.concurrent.duration._

object Collector extends App {

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  // TODO this is for testing purposes
  val metricsProjects =
    Seq(
      MetricsConf.Project(new File("ANY_GIT_FOLDER"), 15.seconds)
    )

  metricsProjects
    .map(project =>
      CustomJobBuilder(
        context = MetricsContext.JGit,
        project = project,
        jobData = Map(MetricsContext.JGit.gcKey -> new GC(new FileRepository(project.path.getAbsolutePath)))
      )
    )
    .foreach { case (jobDetail, trigger) => scheduler.scheduleJob(jobDetail, trigger) }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()
}
