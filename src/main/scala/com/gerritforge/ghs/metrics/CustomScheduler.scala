package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import com.gerritforge.ghs.metrics.MetricsData.{FSData, JGitData}
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

final class CustomScheduler(scheduler: Scheduler) {
  def schedule(gitSiteBasePath: File, project: Config.MetricsCollection.Project, metric: Metric): Date = {
    val (jobDetail, trigger) = metric match {
      case jgit @ Metric.JGit =>
        (buildJobDetail(jgit, project, JGitData(gitSiteBasePath, project)), buildTrigger(project, jgit))
      case fs @ Metric.FileSystem =>
        (buildJobDetail(fs, project, FSData(gitSiteBasePath, project)), buildTrigger(project, fs))
    }
    scheduler.scheduleJob(jobDetail, trigger)
  }

  private def buildJobDetail(
      metric: Metric,
      project: Config.MetricsCollection.Project,
      contextData: MetricsData
  ): JobDetail = {
    val data = new JobDataMap()
    data.put("data", contextData)
    JobBuilder
      .newJob(classOf[CollectionJob])
      .withIdentity(JobKey.jobKey(project.name, metric.`type`))
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: Config.MetricsCollection.Project, metric: Metric): Trigger =
    TriggerBuilder.newTrigger
      .withIdentity(TriggerKey.triggerKey(project.name, metric.`type`))
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
