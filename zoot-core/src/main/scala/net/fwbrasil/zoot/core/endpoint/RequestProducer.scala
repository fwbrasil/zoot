package net.fwbrasil.zoot.core.endpoint

import scala.reflect.runtime.universe._
import java.net.URLEncoder
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.response.Response

case class RequestProducer[A <: Api](endpoint: Endpoint[A]) {

    import endpoint._

    val payloadTypeTag =
        sMethod.typeTagArguments.onlyOne

    val payloadIsResponse =
        payloadTypeTag.tpe.erasure =:= typeOf[Response[_]]
    
    val payloadIsResponseString =
        payloadTypeTag.tpe <:< typeOf[Response[String]]

    val javaMethod =
        sMethod.javaMethodOption
            .getOrElse(throw new IllegalStateException(s"Can't find the java method for $sMethod."))

    def produceRequest(args: List[Any], mapper: StringMapper) = {
        val params = sMethod.parameters.map(_.name).zip(args).toMap
        val pathString = template.path.forParameters(params(_).toString).toString
        Request(
            pathString,
            template.method,
            params.mapValues(encode(_, mapper)),
            Map("Content-Type" -> mapper.contentType)
        )
    }

    private def encode(value: Any, mapper: StringMapper) =
        value match {
            case value: String =>
                value
            case other =>
                URLEncoder.encode(mapper.toString(value), "UTF-8")
        }
}
