package com.gerritforge.ghs.http

import com.gerritforge.ghs.http.GhsMetricsServer.{AuthorizationHeader, shouldUseCompression}
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.config.Config
import kamon.prometheus.ScrapeSource
import kamon.prometheus.embeddedhttp.EmbeddedHttpServer

import java.net.{InetAddress, InetSocketAddress}
import java.nio.charset.StandardCharsets
import java.util
import java.util.zip.GZIPOutputStream
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.Using

/** A copy of the [[kamon.prometheus.embeddedhttp.SunEmbeddedHttpServer]] but with Authorization checking capabilities.
  */
class GhsMetricsServer(hostname: String, port: Int, path: String, scrapeSource: ScrapeSource, config: Config)
    extends EmbeddedHttpServer(hostname, port, scrapeSource, config) {
  private val server = {
    val s = HttpServer.create(new InetSocketAddress(InetAddress.getByName(hostname), port), 0)
    s.setExecutor(null)
    val handler = new HttpHandler {
      override def handle(httpExchange: HttpExchange): Unit = {
        val authorizationToken = config.getString("ghs.prometheusBearerToken")
        val authorizationHeaderValue = httpExchange.getRequestHeaders.asScala
          .get(AuthorizationHeader)
          .map(_.asScala.head)
        authorizationHeaderValue match {
          case Some(token) if token == s"Bearer $authorizationToken" =>
            if (httpExchange.getRequestURI.getPath == path) {
              val data  = scrapeSource.scrapeData()
              val bytes = data.getBytes(StandardCharsets.UTF_8)
              Using.Manager { use =>
                if (shouldUseCompression(httpExchange)) {
                  httpExchange.getResponseHeaders.set("Content-Encoding", "gzip")
                  httpExchange.sendResponseHeaders(200, 0)
                  val os = use(new GZIPOutputStream(httpExchange.getResponseBody))
                  os.write(bytes)
                } else {
                  val os = use(httpExchange.getResponseBody)
                  httpExchange.sendResponseHeaders(200, bytes.length.toLong)
                  os.write(bytes)
                }
              }: Unit
            } else httpExchange.sendResponseHeaders(404, -1)
          case _ => httpExchange.sendResponseHeaders(401, -1)
        }
      }
    }

    s.createContext(path, handler)
    s.start()
    s
  }

  def stop(): Unit = server.stop(0)
}

object GhsMetricsServer {

  val AuthorizationHeader = "Authorization"

  def shouldUseCompression(httpExchange: HttpExchange): Boolean = {
    httpExchange.getRequestHeaders.asScala
      .get("Accept-Encoding")
      .map(extractEncodings)
      .exists(_.contains("gzip"))
  }

  private def extractEncodings(headerList: util.List[String]): mutable.Buffer[String] = {
    headerList.asScala
      .flatMap(_.split(","))
      .map(_.trim().toLowerCase())
  }
}
