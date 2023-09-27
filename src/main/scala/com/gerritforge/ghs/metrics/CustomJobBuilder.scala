package com.gerritforge.ghs.metrics

import com.gerritforge.ghs.Config
import com.gerritforge.ghs.metrics.MetricsData.FSRecord
import org.eclipse.jgit.internal.storage.file.{FileRepository, GC}

import java.io.File
import java.nio.file.Files
import scala.jdk.CollectionConverters.IteratorHasAsScala

final case class MetricsData(projectName: String, collectFn: Function[Unit, AnyRef])

object MetricsData {
  def jgit(gitSiteBasePath: File, project: Config.MetricsCollection.Project): MetricsData = {
    val repository                             = gitSiteBasePath.getAbsolutePath + "/" + project.name
    val gc                                     = new GC(new FileRepository(repository))
    val gcF: Function[Unit, GC.RepoStatistics] = _ => gc.getStatistics
    MetricsData(project.name, gcF)
  }

  case class FSRecord(
      numberOfKeepFilesCount: Long = 0,
      numberOfEmptyDirectoriesCount: Long = 0,
      numberOfDirectoriesCount: Long = 0,
      numberOfFilesCount: Long = 0
  )

  object FSRecord {
    val empty = FSRecord()
  }

  def fs(gitSiteBasePath: File, project: Config.MetricsCollection.Project): MetricsData = {
    val repository                 = gitSiteBasePath.getAbsolutePath + "/" + project.name
    val objectsDirectoryPath: File = new FileRepository(repository).getObjectsDirectory

    def fileSystemRecord(): FSRecord = Files
      .walk(objectsDirectoryPath.toPath)
      .iterator()
      .asScala
      .foldLeft(FSRecord.empty) { (fsRecord, path) =>
        var fSRecordTemp: FSRecord = FSRecord.empty
        val f                      = path.toFile
        if (f.isFile) {
          fSRecordTemp = fsRecord.copy(numberOfFilesCount = fsRecord.numberOfFilesCount + 1)
          if (f.getName.endsWith(".keep")) {
            fSRecordTemp = fsRecord.copy(numberOfKeepFilesCount = fsRecord.numberOfKeepFilesCount + 1)
          }
        } else {
          fSRecordTemp = fsRecord.copy(numberOfDirectoriesCount = fsRecord.numberOfDirectoriesCount + 1)
          if (Option(f.listFiles).getOrElse(Array.empty).isEmpty) {
            fSRecordTemp = fsRecord.copy(numberOfEmptyDirectoriesCount = fsRecord.numberOfEmptyDirectoriesCount + 1)
          }
        }
        fSRecordTemp
      }

    val fileSystemF: Function[Unit, FSRecord] = _ => fileSystemRecord()
    MetricsData(project.name, fileSystemF)
  }
}
