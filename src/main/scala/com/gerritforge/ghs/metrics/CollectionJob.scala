package com.gerritforge.ghs.metrics

import org.quartz.{Job, JobExecutionContext}
class CollectionJob extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    val jobDataMap  = context.getJobDetail.getJobDataMap
    val metricsData = jobDataMap.get("data").asInstanceOf[MetricsData]
    metricsData.collectFn()
  }
}
