package net.fwbrasil.zoot.core

import java.nio.charset.Charset

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.Mirror

import net.fwbrasil.zoot.core.endpoint.Endpoint
import net.fwbrasil.zoot.core.endpoint.RequestConsumer
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable

case class Server[A <: Api: Manifest](instance: A)(
    implicit mapper: StringMapper,
    exctx: ExecutionContext,
    mirror: Mirror,
    encoders: List[Encoder[_]] = List(),
    charset: Charset = Charset.forName("UTF-8"))
    extends (Request => Future[Response[Array[Byte]]]) {

    private val consumers =
        Endpoint.listFor[A].map(new RequestConsumer(_, encoders.asInstanceOf[List[Encoder[Any]]]))

    def apply(request: Request) =
        consumers.findDefined { consumer =>
            consumer.consumeRequest(request, instance, mapper).map { value =>
                consumer.endpoint.responseEncoder.encode(value, charset)
            }
        }.getOrElse {
            Future.successful(Response(body = Array(), status = ResponseStatus.NOT_FOUND))
        }
}