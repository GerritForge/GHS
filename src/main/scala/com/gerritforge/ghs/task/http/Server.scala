package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Tasks
import com.gerritforge.ghs.task.Tasks.Project
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.netty.{NettyFutureServer, NettyFutureServerBinding}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Server {
  val tasksPath = endpoint.in("tasks")

  def tasksProjectEndpoint(project: Project) =
    tasksPath
      .in(project.name)
      .post
      .out(emptyOutput and statusCode(StatusCode.Accepted))
      .serverLogicSuccess { _ =>
        println(s"Call ${project.command} ${project.parameters}")
        Future.unit
      }

  def binding(tasks: Tasks): Future[NettyFutureServerBinding] = {
    val endpoints = tasks.projects.map(tasksProjectEndpoint)
    NettyFutureServer().addEndpoints(endpoints).start()
  }
}
