package com.gerritforge.ghs.task

final case class Task(name: String, inputs: List[String], command: String, parameters: String)
