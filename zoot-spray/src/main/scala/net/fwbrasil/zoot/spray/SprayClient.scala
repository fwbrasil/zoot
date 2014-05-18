package net.fwbrasil.zoot.spray

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.spray.request.requestToSpray
import net.fwbrasil.zoot.spray.response.responseFromSpray
import spray.can.Http
import spray.http.HttpResponse

case class SprayClient(host: String, port: Int)(implicit system: ActorSystem, timeout: Timeout)
    extends (Request => Future[Response[Array[Byte]]]) {

    import system.dispatcher

    private val Http.HostConnectorInfo(connector, _) =
        Await.result(IO(Http) ? Http.HostConnectorSetup(host, port), Duration.Inf)

    def apply(request: Request) =
        connector.ask(requestToSpray(request))
            .mapTo[HttpResponse]
            .map(responseFromSpray(_))
}