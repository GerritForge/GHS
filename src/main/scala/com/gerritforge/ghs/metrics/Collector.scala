package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.quartz.{
  JobBuilder,
  JobDataMap,
  JobDetail,
  JobKey,
  SimpleScheduleBuilder,
  SimpleTrigger,
  Trigger,
  TriggerBuilder,
  TriggerKey
}
import org.quartz.impl.StdSchedulerFactory

import scala.concurrent.duration._
import java.util.Properties

object Collector extends App {

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  val metricsProjects =
    Seq(
      MetricsConf.Project("project1", 5.seconds),
      MetricsConf.Project("project2", 10.seconds),
      MetricsConf.Project("project3", 7.seconds)
    )

  metricsProjects
    .map(p => buildJobDetailsAndTrigger(new CollectionJob.JGit(), p))
    .foreach { case (jobDetail, trigger) =>
      scheduler.scheduleJob(jobDetail, trigger)
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()

  private def buildJobDetailsAndTrigger(
      collectionJob: CollectionJob,
      project: MetricsConf.Project
  ): (JobDetail, Trigger) = {
    (buildJobDetails(collectionJob, project), buildTrigger(project, collectionJob))
  }

  private def buildJobDetails(collectionJob: CollectionJob, project: MetricsConf.Project): JobDetail = {
    val data = new JobDataMap()
    data.put(CollectionJob.MetricTypeKey, collectionJob.metricName)
    data.put(CollectionJob.ProjectKey, project)
    JobBuilder
      .newJob(collectionJob.getClass)
      .withIdentity(JobKey.jobKey(project.name, collectionJob.metricName))
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: MetricsConf.Project, collectionJob: CollectionJob): SimpleTrigger =
    TriggerBuilder.newTrigger
      .withIdentity(TriggerKey.triggerKey(project.name, collectionJob.metricName))
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
