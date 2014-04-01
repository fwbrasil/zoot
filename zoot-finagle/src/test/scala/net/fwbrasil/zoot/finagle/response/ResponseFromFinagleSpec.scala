package net.fwbrasil.zoot.finagle.response

import net.fwbrasil.zoot.finagle.Spec
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpVersion
import org.jboss.netty.handler.codec.http.HttpMethod
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import com.twitter.finagle.http.{ Response => FinagleResponse }
import net.fwbrasil.zoot.core.response._

class ResponseFromFinagleSpec extends Spec {

    "apply" in {
        val response = FinagleResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
        val body = "body"
        response.setContentString(body)
        response.addHeader("someHeader", "someValue")

        responseFromFinagle(response) shouldBe
            Response(body, ResponseStatus.OK, Map("someHeader" -> "someValue"))
    }
}