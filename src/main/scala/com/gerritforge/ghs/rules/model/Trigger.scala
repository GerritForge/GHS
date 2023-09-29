package com.gerritforge.ghs.rules.model

sealed trait Trigger

final case class NumberOfKeepFiles(threshold: Int) extends Trigger
