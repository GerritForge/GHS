package com.gerritforge.ghs.metrics

import org.quartz.{Job, JobExecutionContext}
final class CollectionJob extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    println("do something")
  }
}
