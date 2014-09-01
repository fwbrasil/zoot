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
import net.fwbrasil.zoot.core.Encoder

case class RequestProducer[A <: Api](endpoint: Endpoint[A], hostHeader: Option[String], encoders: List[Encoder[Any]]) {

    import endpoint._

    val javaMethod =
        sMethod.javaMethodOption
            .getOrElse(throw new IllegalStateException(s"Can't find the java method for $sMethod."))

    private val encodersByClass =
        encoders.map(e => e.cls -> e).toMap

    def produceRequest(args: List[Any], mapper: StringMapper) = {
        var request = createRequest(args, mapper)
        for (((cls, param), value) <- parametersByClass.zip(args)) {
            encodersByClass.get(cls) match {
                case Some(encoder) =>
                    request = encoder.encode(value, request)
                case None =>
                    request = request.addParam(param.name, encode(value, mapper))
            }
        }
        request
    }

    private def createRequest(args: List[Any], mapper: StringMapper) = {
        val params = sMethod.parameters.map(_.name).zip(args).toMap
        var request =
            Request(
                pathString(mapper, params),
                template.method,
                Map(),
                Map("Content-Type" -> mapper.contentType) +
                    ("Host" -> hostHeader.getOrElse("undefined"))
            )
        request
    }

    private def pathString(mapper: StringMapper, params: Map[String, Any]) =
        template.path.forParameters(pathParam(_, params, mapper)).toString

    private def pathParam(name: String, params: Map[String, Any], mapper: StringMapper) =
        encode(params(name), mapper)

    private def encode(value: Any, mapper: StringMapper): String =
        value match {
            case value: String =>
                encode(value)
            case other =>
                val string = mapper.toString(value)
                val unescaped = mapper.unescapeString(string)
                encode(unescaped)
        }

    private def encode(string: String) =
        URLEncoder.encode(string, "UTF-8")
}
