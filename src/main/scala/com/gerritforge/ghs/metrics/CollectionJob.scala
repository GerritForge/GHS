package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.SampleApplication.{Metrics, Project}
import org.quartz.{Job, JobExecutionContext}

import java.time.Instant

sealed trait CollectionJob extends Job {
  def metricName: String
}

case object JGit extends CollectionJob {
  override def execute(context: JobExecutionContext): Unit = {
    val jobDataMap         = context.getJobDetail.getJobDataMap
    val jgclient: String   = jobDataMap.getString("jgitClient")
    val project: Project   = jobDataMap.get("project").asInstanceOf[Project]
    val myContext: Metrics = jobDataMap.get("myContext").asInstanceOf[Metrics]
    println(s"Before:  $jgclient  $project  ${myContext} at: ${Instant.now()}")
    Thread.sleep(5000)
    println(s"After:  $jgclient  $project  ${myContext} at: ${Instant.now()}")
  }

  override def metricName: String = "jgit"
}
