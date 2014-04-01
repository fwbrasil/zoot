package net.fwbrasil.zoot.spray.request

import net.fwbrasil.zoot.spray.Spec
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import spray.http.HttpRequest
import spray.http.HttpMethods
import spray.http.HttpHeaders._

class RequestFromSpraySpec extends Spec {

    "apply" in {
        val request = HttpRequest(HttpMethods.GET, "/path?param=value", headers = List(RawHeader("someHeader", "someValue")))
        requestFromSpray(request) shouldBe
            Request("/path", RequestMethod.GET, Map("param" -> "value"), Map("someHeader" -> "someValue"), Some(request))
    }
}