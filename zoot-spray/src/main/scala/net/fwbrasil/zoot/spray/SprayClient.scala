package net.fwbrasil.zoot.spray

import net.fwbrasil.zoot.core.request.Request
import scala.concurrent.Future
import net.fwbrasil.zoot.core.response.Response
import spray.http.HttpResponse
import akka.io.IO
import spray.can.Http
import akka.actor.ActorSystem
import akka.pattern.ask
import spray.http.HttpRequest
import spray.http.HttpMethods._
import spray.http.Uri
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import akka.io.IO
import spray.can.Http
import spray.http._
import HttpMethods._
import net.fwbrasil.zoot.core.request.RequestMethod._
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.response.ResponseStatus
import net.fwbrasil.zoot.core.response.ExceptionResponse
import scala.concurrent.Await

case class SprayClient(host: String, port: Int)(implicit system: ActorSystem, timeout: Timeout) extends (Request => Future[Response[String]]) {

    import system.dispatcher

    val Http.HostConnectorInfo(connector, _) = Await.result(IO(Http) ? Http.HostConnectorSetup(host, port), Duration.Inf)

    def apply(request: Request) =
        connector.ask(requestTranslator.toSpray(request))
            .mapTo[HttpResponse]
            .map(responseTranslator.fromSpray)
}