package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.quartz._

import scala.reflect.{ClassTag, classTag}

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
    def apply(metricType: String, project: MetricsConf.Project): Trigger =
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
