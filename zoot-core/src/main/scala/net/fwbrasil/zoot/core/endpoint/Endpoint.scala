package net.fwbrasil.zoot.core.endpoint

import scala.concurrent.Future
import scala.reflect.runtime.universe._

import net.fwbrasil.smirror._
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.api.EndpointAnnotation
import net.fwbrasil.zoot.core.util.RichIterable.RichIterable

case class Endpoint[A <: Api](
    template: EndpointTemplate,
    sMethod: SMethod[A])(
        implicit mirror: Mirror) {

    require(sMethod.returnType == sClassOf[Future[_]],
        s"Endpoint method '${sMethod.name}' should return scala.concurrent.Future.")
}

object Endpoint {

    def listFor[A <: Api: TypeTag](implicit mirror: Mirror) =
        synchronized {
            sClassOf[A].methods
                .zipWith(_.getAnnotation(classOf[EndpointAnnotation]))
                .collect {
                    case (sMethod, Some(endpointAnnotation)) =>
                        Endpoint[A](EndpointTemplate(endpointAnnotation.method, endpointAnnotation.path), sMethod)
                    case (sMethod, None) if (!sMethod.name.contains("$default$") && !sMethod.symbol.isSynthetic) =>
                        throw new IllegalArgumentException(s"An api trait should have only endpoint methods. Invalid: ${sMethod.name}.")
                }
        }
}