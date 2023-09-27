package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.metrics.Collector.Project
import org.quartz.{Job, JobExecutionContext}

import java.time.Instant

sealed trait CollectionJob extends Job {
  def metricName: String
}

object CollectionJob {
  val ProjectKey: String    = "project"
  val MetricTypeKey: String = "metricType"
  final class JGit() extends CollectionJob {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap         = context.getJobDetail.getJobDataMap
      val project: Project   = jobDataMap.get(ProjectKey).asInstanceOf[Project]
      val metricType: String = jobDataMap.get(MetricTypeKey).asInstanceOf[String]
      println(s"Before:  $project  $metricType at: ${Instant.now()}")
      Thread.sleep(5000)
      println(s"After:  $project  $metricType at: ${Instant.now()}")
    }
    override def metricName: String = "jgit"
  }
}
