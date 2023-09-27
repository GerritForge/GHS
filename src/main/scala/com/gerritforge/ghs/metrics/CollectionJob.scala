package com.gerritforge.ghs.metrics

import org.eclipse.jgit.internal.storage.file.GC
import org.quartz.{Job, JobExecutionContext}

sealed trait CollectionJob extends Job {
  def metricName: String
}

object CollectionJob {
  final class JGit() extends CollectionJob {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap = context.getJobDetail.getJobDataMap
      val gc         = jobDataMap.get("gc").asInstanceOf[GC]
      println(s"statistics: ${gc.getStatistics}")
    }
    override def metricName: String = "jgit"
  }
}
