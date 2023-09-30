package com.gerritforge.ghs.task

// TODO: parameter/commandArgs
final case class Task(name: String, inputs: List[String], command: String, parameters: String)
