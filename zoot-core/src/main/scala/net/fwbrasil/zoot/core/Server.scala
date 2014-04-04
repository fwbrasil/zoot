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

case class Server[A <: Api: Manifest](instance: A)(
    implicit mapper: StringMapper,
    exctx: ExecutionContext,
    mirror: Mirror)
    extends (Request => Future[Response[String]]) {

    val consumers = Endpoint.listFor[A].map(new RequestConsumer(_))

    def apply(request: Request) =
        consumers.findDefined {
            _.consumeRequest(request, instance, mapper)
        }.map { future =>
            future.map { 
                case None =>
                    Response(status = ResponseStatus.NOT_FOUND)
                case value =>
                    Response(mapper.toString(value))
            }.recover {
                case response: ExceptionResponse[_] =>
                    response.asInstanceOf[ExceptionResponse[String]]
            }
        }.getOrElse {
            Future.successful(Response(status = ResponseStatus.NOT_FOUND))
        }
}