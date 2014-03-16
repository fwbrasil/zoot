package net.fwbrasil.zoot.spray.request

import net.fwbrasil.zoot.core.request.Request
import spray.http.HttpRequest

object requestFromSpray {

    def apply(httpRequest: HttpRequest) =
        Request(
            requestMethod.fromSpray(httpRequest.method),
            path(httpRequest),
            params(httpRequest),
            headers(httpRequest))

    private def path(httpRequest: HttpRequest) =
        httpRequest.uri.path.toString

    private def params(httpRequest: spray.http.HttpRequest): Map[String, String] = {
        httpRequest.uri.query.toMap
    }

    private def headers(httpRequest: spray.http.HttpRequest) =
        httpRequest.headers.map(e => e.name -> e.value).toMap
}