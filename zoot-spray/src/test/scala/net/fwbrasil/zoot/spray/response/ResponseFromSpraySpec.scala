package net.fwbrasil.zoot.spray.response

import net.fwbrasil.zoot.spray.Spec
import net.fwbrasil.zoot.core.response._
import spray.http.HttpResponse
import spray.http.StatusCodes
import spray.http.HttpHeaders._

class ResponseFromSpraySpec extends Spec {

    "apply" in {
        val body = "body"
        val response = HttpResponse(StatusCodes.OK, body, List(new RawHeader("someHeader", "someValue")))

        responseFromSpray(response) shouldBe
            Response(ResponseStatus.OK, body, Map("someHeader" -> "someValue"))
    }
}