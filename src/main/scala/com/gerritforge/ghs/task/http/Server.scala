package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Task
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.netty.{NettyFutureServer, NettyFutureServerBinding}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Server {
  def taskEndpoint(task: Task) = {
    val queryArgs = task.inputs.map(query[String](_))
    val queryInput = queryArgs.foldLeft[EndpointInput[_]](emptyInput) { case (acc, s) =>
      acc.and(s)
    }

    endpoint.post
      .in("tasks" / task.name)
      .in(queryInput)
      .out(emptyOutput and statusCode(StatusCode.Accepted))
      .serverLogic { _ =>
        println(s"Call ${task.command} ${task.parameters}")
        Future.successful[Either[Unit, Unit]](Right(()))
      }
  }

  def binding(tasks: List[Task]): Future[NettyFutureServerBinding] = {
    val endpoints = tasks.map(taskEndpoint)
    NettyFutureServer().addEndpoints(endpoints).start()
  }
}
