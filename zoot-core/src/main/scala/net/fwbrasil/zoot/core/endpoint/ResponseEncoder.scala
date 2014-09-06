package net.fwbrasil.zoot.core.endpoint

import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.NormalResponse
import net.fwbrasil.zoot.core.response.ResponseStatus
import net.fwbrasil.zoot.core.mapper.StringMapper
import java.nio.charset.Charset
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class ResponseEncoder(endpoint: Endpoint[_]) {

    def encode(value: Future[Any], charset: Charset)(implicit ctx: ExecutionContext, mapper: StringMapper) =
        value.map {
            case response: Response[_] if endpoint.payloadIsResponseByteArray =>
                response.asInstanceOf[Response[Array[Byte]]]
            case None =>
                Response(body = Array[Byte](), status = ResponseStatus.NOT_FOUND)
            case value =>
                Response(mapper.toString(value).getBytes(charset))
        }.recover {
            case ExceptionResponse(body: String, status, headers) =>
                Response(body.getBytes(charset), status, headers)
        }

    def decode(response: Future[Response[Array[Byte]]], charset: Charset)(implicit ctx: ExecutionContext, mapper: StringMapper) =
        response.map {
            case response if (endpoint.payloadIsResponseByteArray) =>
                response
            case response if (endpoint.payloadIsOption) =>
                if (response.status == ResponseStatus.NOT_FOUND)
                    None
                else
                    Option(mapper.fromString(string(response.body, charset))(endpoint.payloadGenericType.get))
            case response: NormalResponse[_] if (endpoint.payloadIsResponse) =>
                response.copy(body = mapper.fromString(string(response.body, charset))(endpoint.payloadGenericType.get))
            case response if (response.status == ResponseStatus.OK) =>
                mapper.fromString(string(response.body, charset))(endpoint.payloadTypeTag)
            case response =>
                throw new ExceptionResponse(string(response.body, charset), response.status, response.headers)
        }

    private def string(bytes: Array[Byte], charset: Charset) =
        new String(bytes, charset)
}
