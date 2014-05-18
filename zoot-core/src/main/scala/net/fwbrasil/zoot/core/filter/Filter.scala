package net.fwbrasil.zoot.core.filter

import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import scala.concurrent.Future

trait Filter extends ((Request, (Request => Future[Response[Array[Byte]]])) => Future[Response[Array[Byte]]]) {
    self =>

    protected type Service = Request => Future[Response[Array[Byte]]]

    def apply(input: Request, next: Service): Future[Response[Array[Byte]]]

    def andThen(filter: Filter): Filter =
        new Filter {
            override def apply(input: Request, next: Service) =
                self.apply(input, filter(_, next))
        }

    def andThen(service: Service): Service =
        (input: Request) => apply(input, service)
}