package net.fwbrasil.zoot.core.request

import net.fwbrasil.zoot.core.Spec

class RequestSpec extends Spec {

    val path = "/path"

    "method" - {
        def test(method: RequestMethod) =
            Request(path, method).method shouldBe method

        "get" in test(RequestMethod.GET)
        "post" in test(RequestMethod.POST)
        "put" in test(RequestMethod.PUT)
        "delete" in test(RequestMethod.DELETE)
    }

    "path" in {
        Request(path, RequestMethod.POST).path shouldBe path
    }

    "params" - {

        "empty by default" in {
            Request(path, RequestMethod.POST).params shouldBe Map()
        }

        "specified value" in {
            val params = Map("key" -> "value")
            Request(path,RequestMethod.POST,  params = params).params shouldBe params
        }
    }

    "headers" - {

        "empty by default" in {
            Request(path, RequestMethod.POST).headers shouldBe Map()
        }

        "specified value" in {
            val headers = Map("key" -> "value")
            Request(path, RequestMethod.POST, headers = headers).headers shouldBe headers
        }
    }

    "requestPath" in {
        Request(path, RequestMethod.POST).requestPath shouldBe RequestPath(path)
    }
}