package com.gerritforge.ghs.task

import scala.concurrent.duration.FiniteDuration

final case class Task(name: String, inputs: List[String], command: List[String], timeout: Option[FiniteDuration] = None)
