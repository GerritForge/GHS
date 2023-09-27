package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory

import java.io.File
import java.nio.file.Files
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
      MetricsConf.Project(new File("/Users/alvarovilaplana/src/pull-replication/.git"), 5.seconds),
      MetricsConf.Project(Files.createTempDirectory("project1").toFile, 5.seconds),
      MetricsConf.Project(Files.createTempDirectory("project2").toFile, 10.seconds),
      MetricsConf.Project(Files.createTempFile("project3", ".txt").toFile, 7.seconds) // TO TEST MANUALLY THAT FAILS
    )

  metricsProjects
    .map(project =>
      buildJobDetailsAndTrigger(
        new CollectionJob.JGit(),
        project,
        new GC(new FileRepository(project.path.getAbsolutePath))
      )
    )
    .foreach { case (jobDetail, trigger) =>
      scheduler.scheduleJob(jobDetail, trigger)
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()

  private def buildJobDetailsAndTrigger(
      collectionJob: CollectionJob,
      project: MetricsConf.Project,
      gc: GC
  ): (JobDetail, Trigger) = {
    (
      buildJobDetails(collectionJob, project, gc),
      buildTrigger(project, collectionJob)
    )
  }

  private def buildJobDetails(collectionJob: CollectionJob, project: MetricsConf.Project, gc: GC): JobDetail = {
    val data = new JobDataMap()
    data.put("gc", gc)
    JobBuilder
      .newJob(collectionJob.getClass)
      .withIdentity(JobKey.jobKey(project.path.getName, collectionJob.metricName))
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: MetricsConf.Project, collectionJob: CollectionJob): SimpleTrigger =
    TriggerBuilder.newTrigger
      .withIdentity(TriggerKey.triggerKey(project.path.getName, collectionJob.metricName))
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
