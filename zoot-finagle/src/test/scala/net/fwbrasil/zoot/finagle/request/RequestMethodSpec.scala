package net.fwbrasil.zoot.finagle.request

import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.HttpMethod
import net.fwbrasil.zoot.core.request.RequestMethod

class RequestMethodSpec extends Spec {

    "GET" - {
        "fromFinagle" in {
            requestMethod.fromFinagle(HttpMethod.GET) shouldBe RequestMethod.GET
        }
        "toFinagle" in {
            requestMethod.toFinagle(RequestMethod.GET) shouldBe HttpMethod.GET
        }
    }

    "POST" - {
        "fromFinagle" in {
            requestMethod.fromFinagle(HttpMethod.POST) shouldBe RequestMethod.POST
        }
        "toFinagle" in {
            requestMethod.toFinagle(RequestMethod.POST) shouldBe HttpMethod.POST
        }
    }

    "PUT" - {
        "fromFinagle" in {
            requestMethod.fromFinagle(HttpMethod.PUT) shouldBe RequestMethod.PUT
        }
        "toFinagle" in {
            requestMethod.toFinagle(RequestMethod.PUT) shouldBe HttpMethod.PUT
        }
    }

    "DELETE" - {
        "fromFinagle" in {
            requestMethod.fromFinagle(HttpMethod.DELETE) shouldBe RequestMethod.DELETE
        }
        "toFinagle" in {
            requestMethod.toFinagle(RequestMethod.DELETE) shouldBe HttpMethod.DELETE
        }
    }
}