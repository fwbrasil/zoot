package net.fwbrasil.zoot.core

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.Mirror
import net.fwbrasil.zoot.core.endpoint.Endpoint
import net.fwbrasil.zoot.core.endpoint.RequestConsumer
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import java.nio.charset.Charset
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.ExceptionResponse

case class Server[A <: Api: Manifest](instance: A)(
    implicit mapper: StringMapper,
    exctx: ExecutionContext,
    mirror: Mirror,
    charset: Charset = Charset.defaultCharset)
    extends (Request => Future[Response[Array[Byte]]]) {

    val consumers = Endpoint.listFor[A].map(new RequestConsumer(_))

    def apply(request: Request) =
        consumers.findDefined { consumer =>
            consumer.consumeRequest(request, instance, mapper).map {
                _.map {
                    case response: Response[_] if consumer.endpoint.payloadIsResponseByteArray =>
                        response.asInstanceOf[Response[Array[Byte]]]
                    case None =>
                        Response(body = Array[Byte](), status = ResponseStatus.NOT_FOUND)
                    case value: String =>
                        Response(value.getBytes(charset))
                    case value =>
                        Response(mapper.toString(value).getBytes(charset))
                }.recover {
                    case response @ ExceptionResponse(body: Array[Byte], status, headers) =>
                        ExceptionResponse(body, status, headers)
                    case response @ ExceptionResponse(body: String, status, headers) =>
                        ExceptionResponse(body.getBytes(charset), status, headers)
                }
            }
        }.getOrElse {
            Future.successful(Response(body = Array[Byte](), status = ResponseStatus.NOT_FOUND))
        }
}