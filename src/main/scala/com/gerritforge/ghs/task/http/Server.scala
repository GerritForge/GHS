package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Task
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.netty.{NettyFutureServer, NettyFutureServerBinding}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Server {
  def tasksProjectEndpoint(task: Task) = {
    var aaa: EndpointInput[_] = query[String](task.inputs.head)
    println(task.inputs)
    println(aaa.show)
    task.inputs.tail.foreach { case s =>
      aaa = aaa.and(query[String](s))
      println(aaa.show)
    }
    val aa: List[EndpointInput.Query[String]] = List("projectName","foobar").map(s => query[String](s))
    val bb: EndpointInput[(String, String)] = aa.head.and(query[String]("bb"))
    val gg = aa.tail.foldLeft[EndpointInput[_]](aa.head){case (acc,s) => acc.and(s)}
    println(gg)
    // aa.reduce(_ and _)
    println(bb)
    // val aa: List[EndpointInput.Query[String]] = task.inputs.map(query[String])
    // val g = {
    //   def foo[A, B](acc: EndpointInput[A], inputs: List[String]): EndpointInput[B] = inputs match {
    //     case Nil => emptyInput.and(acc)
    //     case head :: tail =>
    //       val h = query[String](head)
    //       foo(h.and(head), tail)
    //       // tail.foldLeft[EndpointInput[B]](h) { case (acc, i) => acc.and(query[String](i)) }
    //   }
    //   foo(query[String](task.inputs.head), task.inputs.tail)

    endpoint
      .in("tasks" / task.name)
      // .in(task.inputs.map(i => query[String](i)).foldLeft(emptyInput)(_ and _))
      .in(aaa)
      .post
      .out(emptyOutput and statusCode(StatusCode.Accepted))
      .serverLogic { _ =>
        println(s"Call ${task.command} ${task.parameters}")
        Future.successful[Either[Unit, Unit]](Right(()))
      }
  }

  def binding(tasks: List[Task]): Future[NettyFutureServerBinding] = {
    val endpoints = tasks.map(tasksProjectEndpoint)
    NettyFutureServer().addEndpoints(endpoints).start()
  }
}
