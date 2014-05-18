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

        val spray = responseFromSpray(response)
        spray.body.toList shouldBe "body".getBytes.toList
        spray.status shouldBe ResponseStatus.OK
        spray.headers shouldBe Map("someHeader" -> "someValue")
    }
}