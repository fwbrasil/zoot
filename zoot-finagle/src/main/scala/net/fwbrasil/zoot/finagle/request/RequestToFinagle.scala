package net.fwbrasil.zoot.finagle.request

import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion

import net.fwbrasil.zoot.core.request.Request

object requestToFinagle {

    def apply(request: Request) = {
        val httpRequest =
            new DefaultHttpRequest(
                HttpVersion.HTTP_1_1,
                requestMethod.toFinagle(request.method),
                request.path + paramsString(request))
        addHeaders(request, httpRequest)
        httpRequest
    }

    private def paramsString(request: Request) =
        request.params.map {
            case (k, v) =>
                k + '=' + v
        } mkString ("?", "&", "")

    private def addHeaders(request: Request, httpRequest: DefaultHttpRequest) =
        for ((key, value) <- request.headers)
            httpRequest.addHeader(key, value)
}