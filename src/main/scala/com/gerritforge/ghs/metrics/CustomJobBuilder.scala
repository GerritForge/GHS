package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.config.MetricsConf
import org.quartz._

sealed trait MetricsContext {
  def metricName: String
}

object MetricsContext {
  case object JGit extends MetricsContext {
    val metricName: String = "jgit"
    val gcKey              = "gc"
    val projectKey         = "project"
    val metricKey          = "metric"
  }
}
object CustomJobBuilder {
  def apply(context: MetricsContext, project: MetricsConf.Project, jobData: Map[String, AnyRef]): (JobDetail, Trigger) =
    context match {
      case git @ MetricsContext.JGit =>
        (buildJobDetails[CollectionJob.JGit](project, git.metricName, jobData), buildTrigger(project, git.metricName))
    }

  private def buildJobDetails[T <: Job](
      project: MetricsConf.Project,
      metricName: String,
      jobData: Map[String, AnyRef]
  )(implicit m: Manifest[T]): JobDetail = {
    val data = new JobDataMap()
    jobData.foreach { case (key, value) => data.put(key, value) }
    JobBuilder
      .newJob(m.runtimeClass.asInstanceOf[Class[T]])
      .withIdentity(JobKey.jobKey(project.path.getAbsolutePath, metricName))
      .usingJobData(data)
      .build
  }

  private def buildTrigger(project: MetricsConf.Project, metricName: String): SimpleTrigger =
    TriggerBuilder.newTrigger
      .withIdentity(TriggerKey.triggerKey(project.path.getName, metricName))
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule
          .withIntervalInSeconds(project.frequency.toSeconds.toInt)
          .repeatForever
      )
      .startNow
      .build
}
