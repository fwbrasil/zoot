package net.fwbrasil.zoot.spray.request

import net.fwbrasil.zoot.spray.Spec
import net.fwbrasil.zoot.core.request.RequestMethod
import spray.http.HttpMethods

class RequestMethodSpec extends Spec {

    "GET" - {
        "fromSpray" in {
            requestMethod.fromSpray(HttpMethods.GET) shouldBe RequestMethod.GET
        }
        "toSpray" in {
            requestMethod.toSpray(RequestMethod.GET) shouldBe HttpMethods.GET
        }
    }

    "POST" - {
        "fromSpray" in {
            requestMethod.fromSpray(HttpMethods.POST) shouldBe RequestMethod.POST
        }
        "toSpray" in {
            requestMethod.toSpray(RequestMethod.POST) shouldBe HttpMethods.POST
        }
    }

    "PUT" - {
        "fromSpray" in {
            requestMethod.fromSpray(HttpMethods.PUT) shouldBe RequestMethod.PUT
        }
        "toSpray" in {
            requestMethod.toSpray(RequestMethod.PUT) shouldBe HttpMethods.PUT
        }
    }

    "DELETE" - {
        "fromSpray" in {
            requestMethod.fromSpray(HttpMethods.DELETE) shouldBe RequestMethod.DELETE
        }
        "toSpray" in {
            requestMethod.toSpray(RequestMethod.DELETE) shouldBe HttpMethods.DELETE
        }
    }
}