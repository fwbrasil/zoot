package net.fwbrasil.zoot.finagle.response

import org.jboss.netty.handler.codec.http.HttpVersion
import com.twitter.finagle.http.{ Response => FinagleResponse }
import net.fwbrasil.zoot.core.response.Response
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers

object responseToFinagle {

    def apply(response: Response[Array[Byte]]) = {
        val httpStatus = responseStatus.toFinagle(response.status)
        val finagleResponse = FinagleResponse(HttpVersion.HTTP_1_1, httpStatus)
        finagleResponse.setContent(ChannelBuffers.copiedBuffer(response.body))
        for ((name, value) <- response.headers)
            finagleResponse.addHeader(name, value)
        finagleResponse
    }
}