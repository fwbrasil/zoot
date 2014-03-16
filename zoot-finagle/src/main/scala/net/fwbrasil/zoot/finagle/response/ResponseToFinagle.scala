package net.fwbrasil.zoot.finagle.response

import org.jboss.netty.handler.codec.http.HttpVersion

import com.twitter.finagle.http.{Response => FinagleResponse}

import net.fwbrasil.zoot.core.response.Response

object responseToFinagle {

    def apply(response: Response[String]) = {
        val httpStatus = responseStatus.toFinagle(response.status)
        val finagleResponse = FinagleResponse(HttpVersion.HTTP_1_1, httpStatus)
        finagleResponse.setContentString(response.body)
        finagleResponse.setContentType("application/json")
        finagleResponse
    }
}