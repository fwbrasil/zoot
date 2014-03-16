package net.fwbrasil.zoot.finagle

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion

import com.twitter.finagle.Service
import com.twitter.finagle.builder.Server

import FutureBridge.scalaToTwitter
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.finagle.request.requestFromFinagle
import net.fwbrasil.zoot.finagle.response.responseToFinagle

class FinagleServer(
    requestConsumer: Request => Option[Future[Response[String]]],
    httpServerBuilder: Service[HttpRequest, HttpResponse] => Server)(implicit ctx: ExecutionContext) {

    val rootService = new Service[HttpRequest, HttpResponse] {

        def apply(httpRequest: HttpRequest) =
            requestConsumer(requestFromFinagle(httpRequest)) match {
                case Some(future) =>
                    future.map(responseToFinagle(_))
                case None =>
                    notFound
            }
    }

    private def notFound =
        Future.successful(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))

    httpServerBuilder(rootService)
}