package net.fwbrasil.zoot.spray.response

import net.fwbrasil.zoot.spray.Spec
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus
import scala.collection.JavaConversions._
import spray.http.StatusCodes
import spray.http.HttpResponse

class ResponseToSpraySpec extends Spec {

    "apply" in {
        val body = "body"
        val response = Response(body.getBytes, ResponseStatus.CONFLICT, Map("someHeader" -> "someValue"))
        val spray = responseToSpray(response)
        spray.status shouldBe StatusCodes.Conflict
        spray.entity.asString shouldBe body
        headers(spray) shouldBe Map("someHeader" -> "someValue")
    }

    private def headers(httpRequest: HttpResponse) =
        httpRequest.headers.map(e => (e.name, e.value)).toMap
}