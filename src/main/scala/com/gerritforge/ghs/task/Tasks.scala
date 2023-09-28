package com.gerritforge.ghs.task

final case class Tasks(projects: List[Tasks.Project])

object Tasks {
  final case class Project(name: String, command: String, parameters: String)
}
