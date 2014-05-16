package net.fwbrasil.zoot.finagle.request

import com.twitter.finagle.http.{Request => FinagleRequest, Response => FinagleResponse }
import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpMethod
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod

class RequestFromFinagleSpec extends Spec {

    "apply" - {
        def test(params: String, expectedParams: Map[String, String] = Map()) = {
            val request = FinagleRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, s"/path$params")
            request.addHeader("someHeader", "someValue")
            requestFromFinagle(request) shouldBe
                Request("/path", RequestMethod.GET, expectedParams, Map("someHeader" -> "someValue"), Some(request))
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