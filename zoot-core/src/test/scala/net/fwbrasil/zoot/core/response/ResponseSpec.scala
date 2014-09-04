package net.fwbrasil.zoot.core.response

import net.fwbrasil.zoot.core.Spec

class ResponseSpec extends Spec {

    "default builder" in {
        Response() shouldBe NormalResponse()
    }

    val status = ResponseStatus.CONTINUE
    val body = "body"
    val headers = Map("a" -> "b")

    "normal response" - {

        "default values" in {
            NormalResponse() shouldBe NormalResponse()
        }

        "specified values" in {
            val response = NormalResponse(body, status, headers)
            response.status shouldBe status
            response.body shouldBe body
            response.headers shouldBe headers
        }
    }

    "exception response" - {

        "default values" in {
            ExceptionResponse() shouldBe ExceptionResponse("", ResponseStatus.INTERNAL_SERVER_ERROR, Map())
        }

        "specified values" in {
            val response = ExceptionResponse(body, status, headers)
            response.status shouldBe status
            response.body shouldBe body
            response.headers shouldBe headers
        }
    }

    "throw exception response" in {
        val response =
            intercept[ExceptionResponse] {
                throw ExceptionResponse(body, status, headers)
            }
        response.status shouldBe status
        response.body shouldBe body
        response.headers shouldBe headers
    }

}