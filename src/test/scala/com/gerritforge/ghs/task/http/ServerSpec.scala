package com.gerritforge.ghs.task.http

import com.gerritforge.ghs.task.Task
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

    val task     = Task("name", List("arg1", "arg2"), List("echo", "parameters"))
    val endpoint = Server.taskEndpoint(task)

    val backendStub: SttpBackend[Future, Any] = TapirStubInterpreter(SttpBackendStub.asynchronousFuture)
      .whenServerEndpoint(endpoint)
      .thenRunLogic()
      .backend()

    val response = basicRequest
      .post(uri"http://test.com/tasks/${task.name}?arg1=foo&arg2=bar")
      .send(backendStub)

    response.map { r =>
      withClue(r) {
        r.code shouldBe StatusCode.Accepted
        r.body.value shouldBe ""
      }
    }
  }

  it should "run the task asynchronously" in {
    val tempFile = os.temp.dir() / "foo"
    val task     = Task("create-file", List("fileName"), List("touch"))
    val endpoint = Server.taskEndpoint(task)

    val backendStub: SttpBackend[Future, Any] = TapirStubInterpreter(SttpBackendStub.asynchronousFuture)
      .whenServerEndpoint(endpoint)
      .thenRunLogic()
      .backend()

    val response = basicRequest
      .post(uri"http://test.com/tasks/${task.name}?fileName=${tempFile}")
      .send(backendStub)

    response.map { r =>
      r.code shouldBe StatusCode.Accepted
      r.body.value shouldBe ""
      assert(os.exists(tempFile))
    }
  }
}
