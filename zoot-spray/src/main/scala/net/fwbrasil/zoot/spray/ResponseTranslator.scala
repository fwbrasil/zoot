package net.fwbrasil.zoot.spray

import net.fwbrasil.zoot.core.response.Response
import spray.http.HttpResponse

object responseTranslator {

    def fromSpray(response: HttpResponse) = {
        val body = response.entity.asString
        val status = StatusTranslator.fromHttpStatus(response.status)
        val headers = headersMap(response)
        Response(status, body, headers)
    }

    def toSpray(response: Response[String]) = {
        val status = StatusTranslator.toHttpStatus(response.status)
        HttpResponse(status, response.body)
    }

    private def headersMap(response: HttpResponse) =
        response.headers.map(e => (e.name, e.value)).toMap

}