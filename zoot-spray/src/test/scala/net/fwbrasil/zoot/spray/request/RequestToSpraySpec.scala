package net.fwbrasil.zoot.spray.request

import scala.collection.JavaConversions.asScalaBuffer
import net.fwbrasil.zoot.spray.Spec
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import spray.http.HttpMethods
import spray.http.HttpRequest

class RequestToSpraySpec extends Spec {

    "apply" in {
        val uri = "/path?param=value"
        val request = Request(RequestMethod.GET, "/path", Map("param" -> "value"), Map("someHeader" -> "someValue"))
        val spray = requestToSpray(request)
        spray.method shouldBe HttpMethods.GET
        spray.uri.toString shouldBe uri
        headers(spray) shouldBe Map("someHeader" -> "someValue")
    }

    private def headers(httpRequest: HttpRequest) =
        httpRequest.headers.map(e => (e.name, e.value)).toMap
}