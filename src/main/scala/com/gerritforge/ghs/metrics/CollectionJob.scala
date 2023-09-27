package com.gerritforge.ghs.metrics

import org.eclipse.jgit.internal.storage.file.GC
import org.quartz.{Job, JobExecutionContext}

sealed trait CollectionJob extends Job

object CollectionJob {
  final class JGit() extends CollectionJob {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap = context.getJobDetail.getJobDataMap
      val gc         = jobDataMap.get(MetricsContext.JGit.gcKey).asInstanceOf[GC]
      val statistics = gc.getStatistics
      println(
        s"statistics for job name: ${context.getJobDetail.getKey.getName} and job group: ${context.getJobDetail.getKey.getGroup} are $statistics"
      )
    }
  }
}
