package com.gerritforge.ghs.rules

import com.gerritforge.ghs.rules.model.{DeleteKeepFiles, NumberOfKeepFiles, Repo, TooManyKeepFiles}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RulesConfigSpec extends AnyFlatSpec with Matchers {

  "RulesConfig" should "parse the config and return a list of rules" in {
    val expectedRule = TooManyKeepFiles(
      name = "TooManyKeepFiles-GHS",
      description = "Too many gitkeep files present in repo",
      active = true,
      repo = Repo("GHS", "/foo/bar/GHS"),
      trigger = Some(NumberOfKeepFiles(1)),
      action = Some(DeleteKeepFiles())
    )

    RulesConfig.configure() shouldBe List(expectedRule)
  }
}
