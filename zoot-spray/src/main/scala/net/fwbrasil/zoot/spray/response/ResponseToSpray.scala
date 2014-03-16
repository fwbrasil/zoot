package net.fwbrasil.zoot.spray.response

import net.fwbrasil.zoot.core.response.Response
import spray.http.HttpEntity.apply
import spray.http.HttpResponse

object responseToSpray {

    def apply(response: Response[String]) = {
        val status = responseStatus.toSpray(response.status)
        HttpResponse(status, response.body)
    }
}