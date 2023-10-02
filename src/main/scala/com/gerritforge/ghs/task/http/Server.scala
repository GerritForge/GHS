package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Task
import com.typesafe.scalalogging.LazyLogging
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.server.netty.{NettyFutureServer, NettyFutureServerBinding}

import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Server extends LazyLogging {
  // This returns nested tuples. Should we use reduce below? It's cleaner, but creating a list from tuples of unknown
  // length requires shapeless, which we may not want to import yet (or matching on all 22 tuples)
  def taskEndpoint(task: Task) = {
    val queryInput = task.inputs.foldLeft[EndpointInput[_]](emptyInput) { case (acc, s) =>
      acc and query[String](s)
    }
    // println(queryInput)
    // val qi: EndpointInput[_] = task.inputs.map(query[String]).reduce[EndpointInput[_]](_ and _)
    // println(qi)

    @tailrec
    def flattenTuples[A](
        arg: A,
        accE: Either[NotImplementedError, List[String]] = Right(Nil)
    ): Either[NotImplementedError, List[String]] = {
      (accE, arg) match {
        case (l @ Left(_), _)                   => l
        case (Right(acc), (_: Unit, s: String)) => Right(acc :+ s)
        case (Right(acc), (tuple, s: String))   => flattenTuples(tuple, Right(acc :+ s))
        case a                                  => Left(new NotImplementedError(s"Could not flatten $a"))
      }
    }

    // Tasks need their own execution context. This will need to be in config and be tweaked
    implicit val taskExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))

    endpoint.post
      .in("tasks" / task.name)
      .in(queryInput)
      .out(emptyOutput and statusCode(StatusCode.Accepted))
      .errorOut(stringBody)
      .serverLogic { case input =>
        flattenTuples(input) match {
          case Left(nie) => Future.successful(Left(nie.getMessage))
          case Right(flattenedInputs) =>
            val command = task.command ++ flattenedInputs

            println(s"Executing $command")
            Future(os.proc(command).call(propagateEnv = false)).onComplete {
              case Failure(exception) =>
                logger.error(s"Failed to complete task ${task.name}", exception)
              case Success(_) =>
                logger.info(s"Successfully executed $command")
            }

            Future.successful(Right(()))
        }
      }
  }

  def binding(tasks: List[Task]): Future[NettyFutureServerBinding] = {
    val endpoints = tasks.map(taskEndpoint)
    import ExecutionContext.Implicits.global
    NettyFutureServer().addEndpoints(endpoints).start()
  }
}
