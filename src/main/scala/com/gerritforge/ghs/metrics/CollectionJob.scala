package com.gerritforge.ghs.metrics

import org.quartz.{Job, JobExecutionContext}
final class CollectionJob extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    println(s"Job with name: ${context.getJobDetail.getKey.getName} and group: ${context.getJobDetail.getKey.getGroup}")
  }
}
