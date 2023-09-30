package com.gerritforge.ghs

import com.gerritforge.ghs.task.Task
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon
import pureconfig._
import pureconfig.generic.auto._
import sttp.tapir.server.netty.NettyFutureServerBinding

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GhsApp extends App with LazyLogging {
  val config = ConfigSource.default.loadOrThrow[Config]

  val tasks = ConfigSource.file(config.tasksPath).at("tasks").loadOrThrow[List[Task]]
  val binding: Future[NettyFutureServerBinding] = task.http.Server.binding(tasks)

  binding.foreach { b =>
    logger.info(s"Started http server on ${b.hostName}:${b.port}")
  }

  Kamon.init()
  while (true) {
    Thread.sleep(1000)
    Kamon.counter("hello.GHS").withoutTags().increment()
  }
}
