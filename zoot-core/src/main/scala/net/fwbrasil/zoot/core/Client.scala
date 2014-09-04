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
import java.nio.charset.Charset
import net.fwbrasil.zoot.core.response.NormalResponse

object Client {

    def apply[A <: Api: ClassTag](
        dispatcher: Request => Future[Response[Array[Byte]]],
        hostHeader: Option[String] = None,
        encoders: List[Encoder[_]] = List(),
        charset: Charset = Charset.forName("UTF-8"))(
            implicit apiTag: TypeTag[A],
            mirror: Mirror,
            mapper: StringMapper,
            exctx: ExecutionContext) = {

        val producerByJavaMethod =
            Endpoint.listFor[A]
                .map(new RequestProducer(_, hostHeader, encoders.asInstanceOf[List[Encoder[Any]]]))
                .groupByUnique(_.javaMethod)

        def string(bytes: Array[Byte]) =
            new String(bytes, charset)

        Stub[A] {
            (method, args) =>
                producerByJavaMethod.get(method).map { producer =>
                    val response = dispatcher(producer.produceRequest(args.toList, mapper))
                    producer
                        .endpoint
                        .responseEncoder
                        .decode(response, charset)
                }
        }
    }
}