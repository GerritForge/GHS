package com.gerritforge.ghs.rules.model

sealed trait Rule {
  def name: String
  def description: String
  def active: Boolean
}

sealed trait FilebasedRule extends Rule {
  def repo: Repo
  def trigger: Option[Trigger]
  def action: Option[Action]
}

final case class TooManyKeepFiles(
    name: String,
    description: String,
    active: Boolean,
    repo: Repo,
    trigger: Option[Trigger],
    action: Option[Action]
) extends FilebasedRule
