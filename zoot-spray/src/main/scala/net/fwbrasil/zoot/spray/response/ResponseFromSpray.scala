package net.fwbrasil.zoot.spray.response

import net.fwbrasil.zoot.core.response.Response
import spray.http.HttpResponse

object responseFromSpray {

    def apply(response: HttpResponse) = {
        val body = response.entity.data.toByteArray
        val status = responseStatus.fromSpray(response.status)
        val headers = headersMap(response)
        Response(body, status, headers)
    }

    private def headersMap(response: HttpResponse) =
        response.headers.map(e => (e.name, e.value)).toMap

}