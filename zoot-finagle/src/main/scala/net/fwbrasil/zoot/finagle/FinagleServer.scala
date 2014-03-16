package net.fwbrasil.zoot.finagle

import scala.collection.JavaConversions.asScalaBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpVersion

import com.twitter.finagle.Service
import com.twitter.finagle.builder.Server
import com.twitter.finagle.http.{Response => FinagleResponse}

import FutureBridge.scalaToTwitter
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.response.Response

class FinagleServer(
    requestConsumer: Request => Option[Future[Response[String]]],
    httpServerBuilder: Service[HttpRequest, HttpResponse] => Server)(implicit ctx: ExecutionContext) {

    val rootService = new Service[HttpRequest, HttpResponse] {
        def apply(httpRequest: HttpRequest) =
            requestConsumer(request(httpRequest)) match {
                case Some(future) =>
                    future.map(finagleResponse)
                case None =>
                    notFound
            }
    }

    private def request(httpRequest: HttpRequest) = {
        val (path, params) = parseUri(httpRequest)
        val headers = httpRequest.headers.entries.map(e => (e.getKey, e.getValue)).toMap
        Request(
            requestMethod(httpRequest.getMethod),
            path,
            params,
            headers)
    }

    private def parseUri(httpRequest: HttpRequest) =
        httpRequest.getUri.split('?').toList match {
            case path :: Params(params) :: Nil =>
                (path, params)
            case path :: Nil =>
                (path, Map[String, String]())
            case other =>
                throw new IllegalArgumentException("Invalid request uri")
        }

    private def finagleResponse(response: net.fwbrasil.zoot.core.response.Response[String]): com.twitter.finagle.http.Response = {
        val httpStatus = StatusTranslator.toHttpStatus(response.status)
        val finagleResponse = FinagleResponse(HttpVersion.HTTP_1_1, httpStatus)
        finagleResponse.setContentString(response.body)
        finagleResponse.setContentType("application/json")
        finagleResponse
    }

    private def notFound =
        Future.successful(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND))

    object Params {
        def unapply(string: String) =
            Some(string.split("&").map(param).toMap)

        private def param(string: String) =
            string.split("=").toList match {
                case key :: value :: Nil =>
                    key -> value
                case other =>
                    throw new IllegalArgumentException(s"Invalid param value $string")
            }
    }

    httpServerBuilder(rootService)

    private def requestMethod(httpMethod: HttpMethod) =
        httpMethod match {
            case HttpMethod.GET =>
                RequestMethod.GET
            case HttpMethod.POST =>
                RequestMethod.POST
            case HttpMethod.PUT =>
                RequestMethod.PUT
            case HttpMethod.DELETE =>
                RequestMethod.DELETE
        }

}