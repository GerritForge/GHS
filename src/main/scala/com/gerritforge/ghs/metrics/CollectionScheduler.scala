package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config.MetricsCollection
import org.quartz._

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
  def schedule(project: MetricsCollection.Project, metric: Metric): Date = {
    scheduler.scheduleJob(buildJobDetail(project, metric), buildTrigger(project, metric))
  }

  private def buildJobDetail(project: MetricsCollection.Project, metric: Metric): JobDetail = {
    JobBuilder
      .newJob(classOf[CollectionJob])
      .withIdentity(project.name, metric.`type`)
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
