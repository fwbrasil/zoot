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
    encoders: List[Encoder[_]] = List(),
    charset: Charset = Charset.forName("UTF-8"))
    extends (Request => Future[Response[Array[Byte]]]) {

    private val consumers = Endpoint.listFor[A].map(new RequestConsumer(_, encoders.asInstanceOf[List[Encoder[Any]]]))

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
                    case ExceptionResponse(body: String, status, headers) =>
                        ExceptionResponse(body.getBytes(charset), status, headers)
                }
            }
        }.getOrElse {
            Future.successful(Response(body = Array(), status = ResponseStatus.NOT_FOUND))
        }
}