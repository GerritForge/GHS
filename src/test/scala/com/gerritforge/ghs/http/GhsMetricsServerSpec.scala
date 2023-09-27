package com.gerritforge.ghs.http

import com.typesafe.config.ConfigFactory
import kamon.prometheus.PrometheusReporter
import org.scalactic.source.Position
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3._

class GhsMetricsServerSpec extends AnyFlatSpec with BeforeAndAfter with Matchers {

  val prometheusReporter = new PrometheusReporter()
  override def after(fun: => Any)(implicit pos: Position): Unit = {
    prometheusReporter.stop()
  }

  "The GhsMetricsServer" should "return an Unauthorised code when Authorization header is missing from the request" in {
    val request = basicRequest.get(uri"http://localhost:9095/metrics")

    val backend  = HttpClientSyncBackend()
    val response = request.send(backend)

    response.code.code shouldBe 401
  }

  it should "return an Unauthorised code when the request authorization token is different to the one in config" in {
    val request = basicRequest
      .header("Authorization", "Bearer wrong-token")
      .get(uri"http://localhost:9095/metrics")

    val backend  = HttpClientSyncBackend()
    val response = request.send(backend)

    response.code.code shouldBe 401
  }

  it should "allow the request if the authorization token matches the one configured in the app" in {
    val appPrometheusToken = {
      val cfg = ConfigFactory.load()
      cfg.getString("ghs.prometheusBearerToken")
    }

    val request = basicRequest
      .header("Authorization", s"Bearer $appPrometheusToken")
      .get(uri"http://localhost:9095/metrics")

    val backend  = HttpClientSyncBackend()
    val response = request.send(backend)

    response.code.code shouldBe 200
  }
}
