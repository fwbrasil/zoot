package net.fwbrasil.zoot.finagle.request

import scala.collection.JavaConversions.asScalaBuffer
import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.DefaultHttpRequest
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpMethod
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import org.jboss.netty.handler.codec.http.HttpRequest

class RequestToFinagleSpec extends Spec {

    "apply" in {
        val uri = "/path?param=value"
        val request = Request(RequestMethod.GET, "/path", Map("param" -> "value"), Map("someHeader" -> "someValue"))
        val finagle = requestToFinagle(request)
        finagle.getMethod shouldBe HttpMethod.GET
        finagle.getUri shouldBe uri
        headers(finagle) shouldBe Map("someHeader" -> "someValue")
    }
    
    private def headers(httpRequest: HttpRequest) =
        httpRequest.headers.entries.map(e => (e.getKey, e.getValue)).toMap
}