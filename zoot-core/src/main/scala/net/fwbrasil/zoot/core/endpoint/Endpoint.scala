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
                    case (sMethod, Some(endpointAnnotation)) if (sMethod.isAbstract) =>
                        Endpoint[A](EndpointTemplate(endpointAnnotation.method, endpointAnnotation.path), sMethod)
                    case (sMethod, Some(endpointAnnotation)) if (!sMethod.isAbstract) =>
                        throw new IllegalArgumentException(s"Endpoint method should be abstract. Invalid: $sMethod.")
                    case (sMethod, None) if (sMethod.isAbstract) =>
                        throw new IllegalArgumentException(s"Only endpoint methods should be abstract. Invalid: $sMethod.")
                }
        }
}