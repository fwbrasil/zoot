package net.fwbrasil.zoot.finagle.request

import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpMethod
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod

class RequestFromFinagleSpec extends Spec {

    "apply" in {
        val request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/path?param=value")
        request.addHeader("someHeader", "someValue")
        requestFromFinagle(request) shouldBe
            Request(RequestMethod.GET, "/path", Map("param" -> "value"), Map("someHeader" -> "someValue"))
    }
}