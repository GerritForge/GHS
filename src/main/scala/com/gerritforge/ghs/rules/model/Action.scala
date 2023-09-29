package com.gerritforge.ghs.rules.model

sealed trait Action

final case class DeleteKeepFiles() extends Action
