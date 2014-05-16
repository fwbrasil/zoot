package net.fwbrasil.zoot.finagle.request

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.asScalaBuffer

import org.jboss.netty.handler.codec.http.HttpRequest
import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse }
import net.fwbrasil.zoot.core.request.Request

object requestFromFinagle {

    def apply(httpRequest: FinagleRequest) = {
        val (path, params) = parseUri(httpRequest)
        Request(
            path,
            requestMethod.fromFinagle(httpRequest.getMethod),
            params,
            headers(httpRequest),
            Some(httpRequest))
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

    private object Params {
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

    private def headers(httpRequest: HttpRequest) =
        httpRequest.headers.entries.map(e => (e.getKey, e.getValue)).toMap

}