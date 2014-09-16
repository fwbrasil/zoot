package net.fwbrasil.zoot.core.endpoint

import scala.concurrent.Future
import scala.reflect.api.Universe
import scala.reflect.runtime.universe._

import net.fwbrasil.smirror.SMethod
import net.fwbrasil.smirror.sClassOf
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.api.EndpointAnnotation
import net.fwbrasil.zoot.core.response.Response
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable

case class Endpoint[A <: Api](
    template: EndpointTemplate,
    sMethod: SMethod[A])(
        implicit mirror: Mirror) {

    require(sMethod.returnType == sClassOf[Future[_]],
        s"Endpoint method '${sMethod.name}' should return scala.concurrent.Future.")

    val payloadTypeTag =
        sMethod.typeTagArguments.onlyOne

    val payloadIsResponse =
        payloadTypeTag.tpe.erasure =:= typeOf[Response[_]]

    val payloadIsResponseByteArray =
        payloadTypeTag.tpe <:< typeOf[Response[Array[Byte]]]

    val payloadIsOption =
        payloadTypeTag.tpe.erasure <:< typeOf[Option[_]]

    val payloadGenericType =
        payloadTypeTag.tpe match {
            case tp: TypeRefApi =>
                tp.args.headOption.map { typ =>
                    new TypeTag[Any] {
                        override def in[U <: Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U#TypeTag[Any] = ???
                        val mirror = sMethod.runtimeMirror
                        def tpe = typ
                    }
                }
            case other =>
                None
        }

    val parametersByClass =
        sMethod.parameters.map(e => e.sClass.javaClassOption.get.asInstanceOf[Class[Any]] -> e)

    def responseEncoder =
        new ResponseEncoder(this)
}

object Endpoint {

    def listFor[A <: Api: TypeTag](implicit mirror: Mirror) =
        synchronized {
            sClassOf[A].methods
                .zipWith(_.getAnnotation(classOf[EndpointAnnotation]))
                .collect {
                    case (sMethod, Some(endpointAnnotation)) if (sMethod.isAbstract) =>
                        Endpoint[A](EndpointTemplate(endpointAnnotation.method, endpointAnnotation.path), sMethod)
                    case (sMethod, Some(endpointAnnotation)) if (!sMethod.isAbstract) =>
                        throw new IllegalArgumentException(s"Endpoint method should be abstract. Invalid: $sMethod.")
                    case (sMethod, None) if (sMethod.isAbstract) =>
                        throw new IllegalArgumentException(s"Only endpoint methods should be abstract. Invalid: $sMethod.")
                }
        }
}