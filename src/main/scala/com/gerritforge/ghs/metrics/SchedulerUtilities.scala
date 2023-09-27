package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.quartz._

import scala.reflect.{ClassTag, classTag}

object SchedulerUtilities {
  object JobDetails {
    def apply[T <: Job: ClassTag](
        metricsType: String,
        projectAbsolutePath: String,
        jobData: Map[String, AnyRef]
    ): JobDetail = {
      val data = new JobDataMap()
      jobData.foreach { case (key, value) => data.put(key, value) }
      JobBuilder
        .newJob(classTag[T].runtimeClass.asInstanceOf[Class[T]])
        .withIdentity(JobKey.jobKey(projectAbsolutePath, metricsType))
        .usingJobData(data)
        .build
    }
  }

  object Trigger {
    def apply(metricsType: String, project: MetricsConf.Project): Trigger =
      TriggerBuilder.newTrigger
        .withIdentity(TriggerKey.triggerKey(project.path.getName, metricsType))
        .withSchedule(
          SimpleScheduleBuilder.simpleSchedule
            .withIntervalInSeconds(project.frequency.toSeconds.toInt)
            .repeatForever
        )
        .startNow
        .build
  }
}
