package net.fwbrasil.zoot.finagle.response

import net.fwbrasil.zoot.finagle.Spec
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus
import org.jboss.netty.handler.codec.http.HttpResponseStatus
import org.jboss.netty.handler.codec.http.HttpResponse
import scala.collection.JavaConversions._

class ResponseToFinagleSpec extends Spec {

    "apply" in {
        val body = "body".getBytes
        val response = Response(body, ResponseStatus.FOUND, Map("someHeader" -> "someValue"))
        val finagle = responseToFinagle(response)
        finagle.getStatus shouldBe HttpResponseStatus.FOUND
        finagle.getContentString shouldBe "body"
        headers(finagle) shouldBe Map("someHeader" -> "someValue")
    }

    private def headers(httpRequest: HttpResponse) =
        httpRequest.headers.entries.map(e => (e.getKey, e.getValue)).toMap
}