package net.fwbrasil.zoot.core

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

import net.fwbrasil.zoot.core.endpoint.Endpoint
import net.fwbrasil.zoot.core.endpoint.RequestProducer
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.NormalResponse
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.response.ResponseStatus
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.util.Stub
import scala.reflect.runtime.universe._

object Client {

    def apply[A <: Api: ClassTag](
        dispatcher: Request => Future[Response[String]])(
            implicit apiTag: TypeTag[A],
            mirror: Mirror,
            mapper: StringMapper,
            exctx: ExecutionContext) = {

        val producerByJavaMethod =
            Endpoint.listFor[A]
                .map(new RequestProducer(_))
                .groupByUnique(_.javaMethod)

        Stub[A] {
            (method, args) =>
                producerByJavaMethod.get(method).map { producer =>
                    dispatcher(producer.produceRequest(args.toList, mapper)).map {
                        case response if (producer.payloadIsResponseString) =>
                            response
                        case response: NormalResponse[_] if (producer.payloadIsResponse) =>
                            response.copy(body = mapper.fromString(response.body)(bodyTypeTag(mirror, producer)))
                        case response: ExceptionResponse[_] if (producer.payloadIsResponse) =>
                            response.copy(body = mapper.fromString(response.body)(bodyTypeTag(mirror, producer)))
                        case response if (response.status == ResponseStatus.OK) =>
                            mapper.fromString(response.body)(producer.payloadTypeTag)
                        case response =>
                            throw new ExceptionResponse(response.body, response.status, response.headers)
                    }
                }
        }
    }

    private def bodyTypeTag(pMirror: Mirror, producer: RequestProducer[_]) =
        new TypeTag[Any] {
            override def in[U <: scala.reflect.api.Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U#TypeTag[Any] = ???
            def tpe = producer.payloadTypeTag.tpe.asInstanceOf[TypeRef].args.head
            val mirror = pMirror
        }
}