package net.fwbrasil.zoot.core.request

import net.fwbrasil.zoot.core.Spec

class RequestSpec extends Spec {

    val path = "/path"

    "method" - {
        def test(method: RequestMethod) =
            Request(method, path).method shouldBe method

        "get" in test(RequestMethod.GET)
        "post" in test(RequestMethod.POST)
        "put" in test(RequestMethod.PUT)
        "delete" in test(RequestMethod.DELETE)
    }

    "path" in {
        Request(RequestMethod.POST, path).path shouldBe path
    }

    "params" - {

        "empty by default" in {
            Request(RequestMethod.POST, path).params shouldBe Map()
        }

        "specified value" in {
            val params = Map("key" -> "value")
            Request(RequestMethod.POST, path, params = params).params shouldBe params
        }
    }

    "headers" - {

        "empty by default" in {
            Request(RequestMethod.POST, path).headers shouldBe Map()
        }

        "specified value" in {
            val headers = Map("key" -> "value")
            Request(RequestMethod.POST, path, headers = headers).headers shouldBe headers
        }
    }

    "requestPath" in {
        Request(RequestMethod.POST, path).requestPath shouldBe RequestPath(path)
    }
}