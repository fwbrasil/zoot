package net.fwbrasil.zoot.spray

import scala.concurrent.Future

import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.util.Timeout
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.spray.request.requestFromSpray
import net.fwbrasil.zoot.spray.response.responseToSpray
import spray.can.Http
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.StatusCodes

case class SprayServer(requestConsumer: Request => Future[Response[String]])(implicit timeout: Timeout)
    extends Actor {

    import context.dispatcher

    def receive = {

        case _: Http.Connected =>
            sender ! Http.Register(self)

        case httpRequest: HttpRequest =>
            val sender = this.sender
            Future.successful({}).flatMap { _ =>
                requestConsumer(requestFromSpray(httpRequest))
                    .map(responseToSpray(_)).map(sender ! _)
            }
    }

}

