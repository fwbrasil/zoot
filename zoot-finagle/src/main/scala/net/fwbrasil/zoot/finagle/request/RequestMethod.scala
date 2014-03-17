package net.fwbrasil.zoot.finagle.request

import org.jboss.netty.handler.codec.http.HttpMethod

import net.fwbrasil.zoot.core.request.RequestMethod

object requestMethod {

    private val mapping = Map(
        HttpMethod.GET -> RequestMethod.GET,
        HttpMethod.POST -> RequestMethod.POST,
        HttpMethod.PUT -> RequestMethod.PUT,
        HttpMethod.DELETE -> RequestMethod.DELETE
    )

    private val reverseMapping =
        mapping.map(_.swap)

    def fromFinagle(method: HttpMethod) =
        mapping(method)

    def toFinagle(method: RequestMethod) =
        reverseMapping(method)
}