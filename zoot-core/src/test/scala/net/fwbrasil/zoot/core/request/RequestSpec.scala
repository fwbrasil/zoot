package net.fwbrasil.zoot.core.request

import net.fwbrasil.zoot.core.Spec

class RequestSpec extends Spec {

    val path = "/path"

    "method" - {
        def test(method: String) =
            Request(path, method).method shouldBe method

        "get" in test(RequestMethod.GET)
        "post" in test(RequestMethod.POST)
        "put" in test(RequestMethod.PUT)
        "delete" in test(RequestMethod.DELETE)
    }

    "path" in {
        Request(path).path shouldBe path
    }

    "params" - {

        "empty by default" in {
            Request(path).params shouldBe Map()
        }

        "specified value" in {
            val params = Map("key" -> "value")
            Request(path, params = params).params shouldBe params
        }
    }

    "headers" - {

        "empty by default" in {
            Request(path).headers shouldBe Map()
        }

        "specified value" in {
            val headers = Map("key" -> "value")
            Request(path, headers = headers).headers shouldBe headers
        }
    }

    "requestPath" in {
        Request(path).requestPath shouldBe RequestPath(path)
    }

    "originalRequest" - {

        "empty by default" in {
            Request(path).originalRequest shouldBe None
        }

        "specified value" in {
            val originalRequest = Some(new Object)
            Request(path, originalRequest = originalRequest).originalRequest shouldBe originalRequest
        }
    }
    
    "host" - {
        
        "request with host header" in {
            val host = "some.com"
            val headers = Map("Host" -> host)
            Request(path, headers = headers).host shouldBe Some(host)
        }
        
        "request without" in {
            Request(path).host shouldBe None
        }
    }
    
    "addHeaders" in {
        val headers = Map("header" -> "value")
        Request(path).addHeaders(headers).headers shouldBe headers
    }
    
    "addParam" in {
        Request(path).addParam("key", "value").params shouldBe Map("key" -> "value")
    }
}