package net.fwbrasil.zoot.finagle

import scala.collection.JavaConversions._
import java.nio.charset.Charset

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion

import com.twitter.finagle.Service

import FutureBridge.twitterToScala
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus

case class FinagleClient(httpService: Service[HttpRequest, HttpResponse])(implicit ctx: ExecutionContext) extends (Request => Future[Response[String]]) {

    def apply(request: Request) =
        handle(
            new DefaultHttpRequest(
                HttpVersion.HTTP_1_1,
                httpMethod(request.method),
                request.path + paramsString(request))
        )

    private def handle(httpRequest: DefaultHttpRequest) =
        httpService(httpRequest).map {
            httpResponse =>
                val body = httpResponse.getContent.toString(Charset.defaultCharset)
                val status = StatusTranslator.fromHttpStatus(httpResponse.getStatus)
                val headers = headersMap(httpRequest)
                Response(status, body, headers)
        }

    private def headersMap(httpResponse: DefaultHttpRequest) =
        httpResponse.getHeaders().map(e => (e.getKey, e.getValue)).toMap

    private def paramsString(request: Request) =
        request.params.map {
            case (k, v) =>
                k + '=' + v
        } mkString ("?", "&", "")

    private def httpMethod(method: RequestMethod) =
        method match {
            case RequestMethod.GET =>
                HttpMethod.GET
            case RequestMethod.PUT =>
                HttpMethod.PUT
            case RequestMethod.POST =>
                HttpMethod.POST
            case RequestMethod.DELETE =>
                HttpMethod.DELETE
        }
}