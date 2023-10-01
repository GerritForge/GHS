package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config.MetricsCollection
import org.quartz._

import java.util.Date
final class CollectionScheduler(scheduler: Scheduler) {
  def schedule(project: MetricsCollection.Project): Date = {
    scheduler.scheduleJob(buildJobDetail(project), buildTrigger(project))
  }

  private def buildJobDetail(project: MetricsCollection.Project): JobDetail = {
    JobBuilder
      .newJob(classOf[CollectionJob])
      .withIdentity(project.name)
      .build
  }

  private def buildTrigger(project: MetricsCollection.Project): Trigger =
    TriggerBuilder.newTrigger
      .withIdentity(project.name)
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
