package net.fwbrasil.zoot.finagle

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse

import com.twitter.finagle.Service

import FutureBridge.twitterToScala
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.finagle.request.requestToFinagle
import net.fwbrasil.zoot.finagle.response.responseFromFinagle

case class FinagleClient(httpService: Service[HttpRequest, HttpResponse])(implicit ctx: ExecutionContext)
    extends (Request => Future[Response[String]]) {

    def apply(request: Request) =
        httpService(requestToFinagle(request))
            .map(responseFromFinagle(_))
}