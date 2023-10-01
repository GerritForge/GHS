package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import kamon.Kamon
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}
import cats.Monoid
import cats.implicits._

import java.io.File
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala

sealed trait MetricsData {
  def collectFn: () => Unit
}

object MetricsData {
  final case class JGitData(gitSiteBasePath: File, project: Config.MetricsCollection.Project) extends MetricsData {
    private val repository = gitSiteBasePath.getAbsolutePath + "/" + project.name
    private val gc         = new GC(new FileRepository(repository))
    override def collectFn: () => Unit = () => {
      val repoStatistics = gc.getStatistics

      List(
        ("numberOfPackedObjects", repoStatistics.numberOfPackedObjects),
        ("numberOfPackFiles", repoStatistics.numberOfPackFiles),
        ("numberOfLooseObjects", repoStatistics.numberOfLooseObjects),
        ("numberOfLooseRefs", repoStatistics.numberOfLooseRefs),
        ("numberOfPackedRefs", repoStatistics.numberOfPackedRefs),
        ("sizeOfLooseObjects", repoStatistics.sizeOfLooseObjects),
        ("sizeOfPackedObjects", repoStatistics.sizeOfPackedObjects),
        ("numberOfBitmaps", repoStatistics.numberOfBitmaps)
      ).foreach { case (metricName, metricValue) =>
        Kamon
          .gauge(metricName)
          .withTag("project", project.name)
          .withTag("metric", Metric.JGit.`type`)
          .update(metricValue.toDouble)
      }
    }
  }

  private final case class FSRecord(
      numberOfKeepFilesCount: Long,
      numberOfEmptyDirectoriesCount: Long,
      numberOfDirectoriesCount: Long,
      numberOfFilesCount: Long
  )

  private object FSRecord {
    implicit val fsRecordMonoid: Monoid[FSRecord] = Monoid.instance(
      emptyValue = FSRecord(0, 0, 0, 0),
      cmb = (a, b) =>
        FSRecord(
          numberOfFilesCount = a.numberOfFilesCount + b.numberOfFilesCount,
          numberOfKeepFilesCount = a.numberOfKeepFilesCount + b.numberOfKeepFilesCount,
          numberOfDirectoriesCount = a.numberOfDirectoriesCount + b.numberOfDirectoriesCount,
          numberOfEmptyDirectoriesCount = a.numberOfEmptyDirectoriesCount + b.numberOfEmptyDirectoriesCount
        )
    )
  }

  final case class FSData(gitSiteBasePath: File, project: Config.MetricsCollection.Project) extends MetricsData {
    private val repository                 = gitSiteBasePath.getAbsolutePath + "/" + project.name
    private val objectsDirectoryPath: File = new FileRepository(repository).getObjectsDirectory

    private def fileSystemRecord(objectsDirectoryPath: File): FSRecord = {
      val paths = Files.walk(objectsDirectoryPath.toPath).iterator().asScala.toList
      paths.foldMap { path =>
        val f = path.toFile
        FSRecord(
          numberOfFilesCount = if (f.isFile) 1 else 0,
          numberOfKeepFilesCount = if (f.isFile && f.getName.endsWith(".keep")) 1 else 0,
          numberOfDirectoriesCount = if (f.isDirectory) 1 else 0,
          numberOfEmptyDirectoriesCount =
            if (f.isDirectory && Option(f.listFiles).getOrElse(Array.empty).isEmpty) 1 else 0
        )
      }
    }

    override def collectFn: () => Unit = () => {
      val fsRecord = fileSystemRecord(objectsDirectoryPath)
      List(
        ("numberOfFilesCount", fsRecord.numberOfFilesCount),
        ("numberOfDirectoriesCount", fsRecord.numberOfDirectoriesCount),
        ("numberOfEmptyDirectoriesCount", fsRecord.numberOfEmptyDirectoriesCount),
        ("numberOfKeepFilesCount", fsRecord.numberOfKeepFilesCount)
      ).foreach { case (metricName, metricValue) =>
        Kamon
          .gauge(metricName)
          .withTag("project", project.name)
          .withTag("metric", Metric.FileSystem.`type`)
          .update(metricValue.toDouble)
      }
    }
  }
}
