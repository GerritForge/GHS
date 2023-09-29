package com.gerritforge.ghs.rules

import com.gerritforge.ghs.rules.model.Rule
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

object RulesConfig {

  def configure(): Seq[Rule] = {
    val rulesConfig = ConfigFactory.load("rules.conf")
    ConfigSource.fromConfig(rulesConfig).at("rules").loadOrThrow[Seq[Rule]]
  }
}
