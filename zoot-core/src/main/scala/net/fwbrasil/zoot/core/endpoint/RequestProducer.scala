package net.fwbrasil.zoot.core.endpoint

import scala.reflect.runtime.universe._
import java.net.URLEncoder
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.response.Response
import scala.reflect.runtime.universe._
import scala.reflect.api.Universe
import scala.reflect.api.Mirror

case class RequestProducer[A <: Api](endpoint: Endpoint[A], hostHeader: Option[String]) {

    import endpoint._

    val javaMethod =
        sMethod.javaMethodOption
            .getOrElse(throw new IllegalStateException(s"Can't find the java method for $sMethod."))

    def produceRequest(args: List[Any], mapper: StringMapper) = {
        val params = sMethod.parameters.map(_.name).zip(args).toMap
        Request(
            pathString(mapper, params),
            template.method,
            params.mapValues(encode(_, mapper)),
            Map("Content-Type" -> mapper.contentType) ++
                hostHeader.map("Host" -> _)
        )
    }

    private def pathString(mapper: StringMapper, params: Map[String, Any]) =
        template.path.forParameters(pathParam(_, params, mapper)).toString

    private def pathParam(name: String, params: Map[String, Any], mapper: StringMapper) =
        params(name) match {
            case string: String =>
                string
            case other =>
                val string = mapper.toString(other)
                mapper.unescapeString(string)
        }

    private def encode(value: Any, mapper: StringMapper) =
        value match {
            case value: String =>
                value
            case other =>
                URLEncoder.encode(mapper.toString(value), "UTF-8")
        }
}
