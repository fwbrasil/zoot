package net.fwbrasil.zoot.finagle.request

import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpMethod
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod

class RequestFromFinagleSpec extends Spec {

    "apply" - {
        def test(params: String, expectedParams: Map[String, String] = Map()) = {
            val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, s"/path$params")
            request.addHeader("someHeader", "someValue")
            requestFromFinagle(request) shouldBe
                Request(RequestMethod.GET, "/path", expectedParams, Map("someHeader" -> "someValue"))
        }

        "empty params" in test("", Map())

        "non empty" in test("?param=value", Map("param" -> "value"))

        "invalid param" in {
            intercept[IllegalArgumentException] {
                test("?invalid")
            }
        }
    }
}