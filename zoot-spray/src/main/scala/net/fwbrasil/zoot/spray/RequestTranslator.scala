package net.fwbrasil.zoot.spray

import spray.http.HttpRequest
import spray.http.HttpMethods
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.request.Request

object requestTranslator {

    def toSpray(request: Request) =
        HttpRequest(
            methodsMap(request.method),
            uri(request))

    def fromSpray(httpRequest: HttpRequest) =
        Request(
            reverseMethodsMap(httpRequest.method),
            path(httpRequest),
            params(httpRequest),
            headers(httpRequest))

    private def paramsString(request: Request) =
        request.params.map {
            case (k, v) =>
                k + '=' + v
        } mkString ("?", "&", "")

    private val methodsMap = Map(
        RequestMethod.GET -> HttpMethods.GET,
        RequestMethod.PUT -> HttpMethods.PUT,
        RequestMethod.POST -> HttpMethods.POST,
        RequestMethod.DELETE -> HttpMethods.DELETE
    )

    private val reverseMethodsMap =
        methodsMap.map(_.swap)

    private def path(httpRequest: HttpRequest) =
        httpRequest.uri.path.toString

    private def params(httpRequest: spray.http.HttpRequest): Map[String, String] = {
        httpRequest.uri.query.toMap
    }

    private def headers(httpRequest: spray.http.HttpRequest) =
        httpRequest.headers.map(e => e.name -> e.value).toMap

    private def uri(request: Request) =
        request.path + paramsString(request)
}