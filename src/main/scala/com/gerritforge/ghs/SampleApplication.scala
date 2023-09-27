package com.gerritforge.ghs

import org.quartz._
import org.quartz.impl.StdSchedulerFactory

import java.time.Instant
import java.util.Properties
import scala.concurrent.duration.FiniteDuration

object SampleApplication extends App {

  final case class Project(name: String, frequency: FiniteDuration)

  sealed trait Metrics {
    def value: String
  }

  object Metrics {
    case object Jgit extends Metrics {
      val value = "jgit"
    }
    case object FileSystem extends Metrics {
      val value = "fileSystem"
    }
  }
  class MetricsJgitJob() extends Job {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap         = context.getJobDetail.getJobDataMap
      val jgclient: String   = jobDataMap.getString("jgitClient")
      val project: Project   = jobDataMap.get("project").asInstanceOf[Project]
      val myContext: Metrics = jobDataMap.get("myContext").asInstanceOf[Metrics]
      println(s"Before:  $jgclient  $project  ${myContext} at: ${Instant.now()}")
      Thread.sleep(5000)
      println(s"After:  $jgclient  $project  ${myContext} at: ${Instant.now()}")
    }
  }

  class MetricsFSJob() extends Job {
    override def execute(context: JobExecutionContext): Unit = {
      val jobDataMap         = context.getJobDetail.getJobDataMap
      val otherStuff: String = jobDataMap.getString("otherStuff")
      val project: Project   = jobDataMap.get("project").asInstanceOf[Project]
      val myContext: Metrics = jobDataMap.get("myContext").asInstanceOf[Metrics]
      println(s"Before:  $otherStuff  $project  ${myContext} at: ${Instant.now()}")
      Thread.sleep(5000)
      println(s"After:  $otherStuff  $project  ${myContext} at: ${Instant.now()}")
    }
  }

  object Scheduler {
    def metricsGitJob(jgit: String, project: Project): (JobDetail, Trigger) = {
      val map = new JobDataMap()
      map.put("jgitClient", jgit)
      map.put("project", project)
      map.put("myContext", Metrics.Jgit)
      (buildJobDetails[MetricsJgitJob](project, map, Metrics.Jgit), buildTrigger(project, Metrics.Jgit))
    }

    def metricsFSJob(otherStuff: String, project: Project): (JobDetail, Trigger) = {
      // Grab the Scheduler instance from the Factory
      val map = new JobDataMap()
      map.put("otherStuff", otherStuff)
      map.put("project", project)
      map.put("myContext", Metrics.FileSystem)

      (buildJobDetails[MetricsFSJob](project, map, Metrics.FileSystem), buildTrigger(project, Metrics.FileSystem))
    }

    private def buildJobDetails[T <: Job](project: Project, data: JobDataMap, context: Metrics)(implicit
        m: Manifest[T]
    ): JobDetail = {
      val a: Class[T] = m.runtimeClass.asInstanceOf[Class[T]]
      JobBuilder
        .newJob(a)
        .withIdentity(JobKey.jobKey(project.name, context.value))
        .usingJobData(data)
        .build
    }

    private def buildTrigger(project: Project, context: Metrics): SimpleTrigger =
      TriggerBuilder.newTrigger
        .withIdentity(TriggerKey.triggerKey(project.name, context.value))
        .withSchedule(
          SimpleScheduleBuilder.simpleSchedule
            .withIntervalInSeconds(project.frequency.toSeconds.toInt)
            .repeatForever
        )
        .startNow
        .build
  }

  import scala.concurrent.duration._

  val metricsProjects =
    Seq(Project("project1", 5.seconds), Project("project2", 10.seconds), Project("project3", 7.seconds))

  val properties = new Properties()
  properties.put("org.quartz.threadPool.threadCount", "10")
  val schedulerFactory = new StdSchedulerFactory(properties)
  val scheduler        = schedulerFactory.getScheduler()

  scheduler.start()

  metricsProjects
    .map(p => Scheduler.metricsGitJob("jgit", p))
    .foreach { case (jobDetail, trigger) =>
      scheduler.scheduleJob(jobDetail, trigger)
    }

  metricsProjects
    .map(p => Scheduler.metricsFSJob("other client", p))
    .foreach { case (jobDetail, trigger) =>
      scheduler.scheduleJob(jobDetail, trigger)
    }

  Thread.sleep(240000)
  scheduler.shutdown()

  // Things to take in account:
  // - job might never run when the execution needs to be serialised
  // - do we have contraints between type of jobs in terms of perfomance, do they clash?
}

// Metrics
// Scheduler
// for each projects of type Jgit metrics => register job, trigger
// for each projects of type FS metrics => register job, trigger

// job => execute =>
//              - jgit collect metrics
//              - print them

// Requirements:
// - serialised per project and paralelise different project
// - thread pool is > 1
