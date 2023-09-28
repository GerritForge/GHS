package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Tasks
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3._
import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCode
import sttp.tapir.server.stub.TapirStubInterpreter

import scala.concurrent.Future

class ServerSpec extends AsyncFlatSpec with Matchers with EitherValues with ScalaFutures {

  it should "respond to the project tasks endpoint" in {

    val project  = Tasks.Project("name", "command", "parameters")
    val endpoint = Server.tasksProjectEndpoint(project)

    val backendStub: SttpBackend[Future, Any] = TapirStubInterpreter(SttpBackendStub.asynchronousFuture)
      .whenServerEndpoint(endpoint)
      .thenRunLogic()
      .backend()

    val response = basicRequest
      .post(uri"http://test.com/tasks/${project.name}")
      .send(backendStub)

    response.map { r =>
      r.code shouldBe StatusCode.Accepted
      r.body.value shouldBe ""
    }
  }
}
