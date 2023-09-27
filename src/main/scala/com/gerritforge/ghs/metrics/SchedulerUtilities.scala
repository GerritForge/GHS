package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import org.quartz._

object SchedulerUtilities {
  object JobDetail {
    def apply(contextData: MetricsData, collectionJob: CollectionJob): JobDetail = {
      val data = new JobDataMap()
      data.put("data", contextData)
      JobBuilder
        .newJob(collectionJob.getClass)
        .withIdentity(JobKey.jobKey(contextData.projectName, collectionJob.metricType))
        .usingJobData(data)
        .build
    }
  }

  object Trigger {
    def apply(metricType: String, project: Config.MetricsCollection.Project): Trigger =
      TriggerBuilder.newTrigger
        .withIdentity(TriggerKey.triggerKey(project.name, metricType))
        .withSchedule(
          SimpleScheduleBuilder.simpleSchedule
            .withIntervalInSeconds(project.frequency.toSeconds.toInt)
            .repeatForever
        )
        .startNow
        .build
  }
}
