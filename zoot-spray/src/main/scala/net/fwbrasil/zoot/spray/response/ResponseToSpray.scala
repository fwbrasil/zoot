package net.fwbrasil.zoot.spray.response

import net.fwbrasil.zoot.core.response.Response
import spray.http.HttpEntity.apply
import spray.http.HttpResponse
import spray.http.HttpHeaders._

object responseToSpray {

    def apply(response: Response[Array[Byte]]) = {
        val status = responseStatus.toSpray(response.status)
        HttpResponse(status, response.body, headers(response).toList)
    }

    private def headers(response: Response[Array[Byte]]) = {
        for ((name, value) <- response.headers) yield {
            new RawHeader(name, value)
        }
    }
}