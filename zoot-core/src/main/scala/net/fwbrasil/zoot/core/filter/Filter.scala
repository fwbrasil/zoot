package net.fwbrasil.zoot.core.filter

import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.response.Response
import scala.concurrent.Future

trait Filter extends ((Request, (Request => Future[Response[String]])) => Future[Response[String]]) {
    self =>

    protected type Service = Request => Future[Response[String]]

    def apply(input: Request, next: Service): Future[Response[String]]

    def andThen(filter: Filter): Filter =
        new Filter {
            override def apply(input: Request, next: Service) =
                self.apply(input, filter(_, next))
        }

    def andThen(service: Service): Service =
        (input: Request) => apply(input, service)
}