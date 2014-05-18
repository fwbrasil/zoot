package net.fwbrasil.zoot.finagle.response

import java.nio.charset.Charset

import scala.collection.JavaConversions.asScalaBuffer

import org.jboss.netty.handler.codec.http.HttpResponse

import net.fwbrasil.zoot.core.response.Response

object responseFromFinagle {

    def apply(httpResponse: HttpResponse) = {
        val body = httpResponse.getContent.array
        val status = responseStatus.fromFinagle(httpResponse.getStatus)
        val headers = headersMap(httpResponse)
        Response(body, status, headers)
    }
    
    private def headersMap(httpResponse: HttpResponse) =
        httpResponse.getHeaders().map(e => (e.getKey, e.getValue)).toMap
}