package net.fwbrasil.zoot.spray

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import akka.actor.Actor
import akka.actor.actorRef2Scala
import akka.util.Timeout
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import spray.can.Http
import spray.http.HttpEntity.apply
import spray.http.HttpMethods
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.StatusCodes
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus

case class SprayServer(requestConsumer: Request => Option[Future[Response[String]]])(implicit timeout: Timeout) extends Actor {

    private implicit val executor = context.dispatcher

    def receive = {

        case _: Http.Connected =>
            sender ! Http.Register(self)

        case httpRequest: HttpRequest =>
            val sender = this.sender
            val request = requestTranslator.fromSpray(httpRequest)
            requestConsumer(request) match {
                case Some(future) =>
                    future.map(responseTranslator.toSpray)
                case None =>
                    sender ! HttpResponse(StatusCodes.NotFound)
            }
    }

}

