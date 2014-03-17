package net.fwbrasil.zoot.spray.request

import net.fwbrasil.zoot.core.request.Request
import spray.http.HttpRequest
import spray.http.Uri.apply
import spray.http.HttpHeaders._

object requestToSpray {

    def apply(request: Request) =
        HttpRequest(
            requestMethod.toSpray(request.method),
            uri(request),
            headers(request).toList)

    private def headers(request: Request) =
        for ((name, value) <- request.headers) yield {
            new RawHeader(name, value)
        }

    private def paramsString(request: Request) =
        request.params.map {
            case (k, v) =>
                k + '=' + v
        } mkString ("?", "&", "")

    private def uri(request: Request) =
        request.path + paramsString(request)
}