package net.fwbrasil.zoot.finagle.request

import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion

import net.fwbrasil.zoot.core.request.Request

object requestToFinagle {

    def apply(request: Request) =
        new DefaultHttpRequest(
            HttpVersion.HTTP_1_1,
            requestMethod.toSpray(request.method),
            request.path + paramsString(request))

    private def paramsString(request: Request) =
        request.params.map {
            case (k, v) =>
                k + '=' + v
        } mkString ("?", "&", "")
}