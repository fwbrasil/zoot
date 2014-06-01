package net.fwbrasil.zoot.spray.request

import net.fwbrasil.zoot.core.request.RequestMethod
import spray.http.HttpMethod
import spray.http.HttpMethods

object requestMethod {

    private val methodsMap = Map(
        RequestMethod.GET -> HttpMethods.GET,
        RequestMethod.PUT -> HttpMethods.PUT,
        RequestMethod.POST -> HttpMethods.POST,
        RequestMethod.DELETE -> HttpMethods.DELETE
    )

    private val reverseMethodsMap =
        methodsMap.map(_.swap)

    def fromSpray(method: HttpMethod) =
        reverseMethodsMap(method)

    def toSpray(method: String) =
        methodsMap(method)
}