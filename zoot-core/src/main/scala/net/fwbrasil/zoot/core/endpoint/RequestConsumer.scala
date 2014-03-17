package net.fwbrasil.zoot.core.endpoint

import java.net.URLDecoder
import scala.concurrent.Future
import net.fwbrasil.smirror.SParameter
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.mapper.StringMapper
import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable
import net.fwbrasil.zoot.core.response.ExceptionResponse
import net.fwbrasil.zoot.core.response.ResponseStatus

case class RequestConsumer[A <: Api](endpoint: Endpoint[A]) {

    import endpoint._

    def consumeRequest(request: Request, instance: A, mapper: StringMapper) =
        template.tryParse(request).map { pathParams =>
            val parameters = values(pathParams, request, instance, mapper)
            try sMethod.invoke(instance, parameters.toSeq: _*).asInstanceOf[Future[Any]]
            catch {
                case e: Throwable =>
                    Future.failed(e)
            }
        }

    private def values(pathParams: Map[String, String], request: Request, instance: A, mapper: StringMapper) = {
        def getParam(name: String) =
            pathParams.get(name)
                .orElse(request.params.get(name))
        verifyMissingParams(paramValues(getParam, instance, mapper))
    }

    private def verifyMissingParams(values: Iterable[(SParameter[A], Option[Any])]) = {
        values.collect {
            case (param, None) if (!param.isOption) => param
        }.ifNonEmpty { missing =>
            throw ExceptionResponse(ResponseStatus.BAD_REQUEST, s"Missing parameters $missing to call $sMethod.")
        }
        values.collect {
            case (param, None) if (param.isOption) => None
            case (param, Some(value)) => value
        }
    }

    private def paramValues(getParam: String => Option[String], instance: A, mapper: StringMapper) =
        parameters.zipWith { param =>
            getParam(param.name)
                .map(readParam(param, _, mapper))
                .orElse(param.defaultValueMethodOption.map(_.invoke(instance)))
        }

    private def readParam(param: SParameter[A], value: String, mapper: StringMapper) =
        try mapper.fromString(URLDecoder.decode(value))(param.typeTag)
        catch {
            case e: Exception =>
                throw ExceptionResponse(ResponseStatus.BAD_REQUEST, s"Invalid value $value for parameter $param.")
        }

    private def parameters =
        sMethod.parameters.asInstanceOf[List[SParameter[A]]]
}
