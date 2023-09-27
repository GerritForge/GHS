package com.gerritforge.ghs.metrics

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

  // XXX: This will need to be removed once integrated with the code loading metrics config from
  // the configuration file
  final case class Project(name: String, frequency: FiniteDuration)

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val scheduler = new StdSchedulerFactory(properties).getScheduler

  scheduler.start()

  val metricsProjects =
    Seq(Project("project1", 5.seconds), Project("project2", 10.seconds), Project("project3", 7.seconds))

  metricsProjects
    .map(p => buildJobDetailsAndTrigger(JGit, p))
    .foreach { case (jobDetail, trigger) =>
      scheduler.scheduleJob(jobDetail, trigger)
    }

  // XXX: Add hook to capture sigint
  println("Starting to sleep...")
  Thread.sleep(240000)
  scheduler.shutdown()

  private def buildJobDetailsAndTrigger(collectionJob: CollectionJob, project: Project): (JobDetail, Trigger) = {
    val map = new JobDataMap()
    map.put("metricType", collectionJob.metricName)
    map.put("project", project)
    (buildJobDetails(collectionJob, project, map), buildTrigger(project, collectionJob))
  }

  private def buildJobDetails(collectionJob: CollectionJob, project: Project, data: JobDataMap): JobDetail = {
    JobBuilder
      .newJob(collectionJob.getClass)
      .withIdentity(JobKey.jobKey(project.name, collectionJob.metricName))
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: Project, collectionJob: CollectionJob): SimpleTrigger =
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
