package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config.MetricsCollection
import org.quartz._

import java.io.File
import java.util.Date

sealed trait Metric {
  def `type`: String
}

object Metric {
  case object JGit extends Metric {
    override val `type` = "jgit"
  }
  case object FileSystem extends Metric {
    override val `type` = "fs"
  }
}

final class CollectionScheduler(scheduler: Scheduler) {
  def schedule(gitSiteBasePath: File, project: MetricsCollection.Project, metric: Metric): Date = {
    val (jobDetail, trigger) = metric match {
      case jgit @ Metric.JGit =>
        (buildJobDetail(project, MetricsData.JGitData(gitSiteBasePath, project), jgit), buildTrigger(project, jgit))
      case fs @ Metric.FileSystem =>
        (buildJobDetail(project, MetricsData.FSData(gitSiteBasePath, project), fs), buildTrigger(project, fs))
    }
    scheduler.scheduleJob(jobDetail, trigger)
  }

  private def buildJobDetail(
      project: MetricsCollection.Project,
      contextData: MetricsData,
      metric: Metric
  ): JobDetail = {
    val data = new JobDataMap()
    data.put("data", contextData)
    JobBuilder
      .newJob(classOf[CollectionJob])
      .withIdentity(project.name, metric.`type`)
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: MetricsCollection.Project, metric: Metric): Trigger =
    TriggerBuilder.newTrigger
      .withIdentity(project.name, metric.`type`)
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
