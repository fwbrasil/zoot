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
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse }

case class FinagleServer(
    requestConsumer: Request => Future[Response[Array[Byte]]],
    httpServerBuilder: Service[FinagleRequest, FinagleResponse] => Server)(implicit ctx: ExecutionContext) {

    private val rootService = new Service[FinagleRequest, FinagleResponse] {

        def apply(httpRequest: FinagleRequest) =
            Future.successful({}).flatMap { _ =>
                requestConsumer(requestFromFinagle(httpRequest)).map(responseToFinagle(_))
            }
    }

    private val server = httpServerBuilder(rootService)

    def close = server.close
}