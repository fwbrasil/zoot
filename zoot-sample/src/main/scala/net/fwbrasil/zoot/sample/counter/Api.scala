package net.fwbrasil.zoot.sample.counter

import net.fwbrasil.zoot.core.endpoint
import net.fwbrasil.zoot.core.Api
import net.fwbrasil.zoot.core.request.RequestMethod._
import scala.concurrent.Future

trait CounterApi extends Api {

    @endpoint(
        method = POST,
        path = "/:name"
    )
    def create(name: String): Future[Unit]

    @endpoint(
        method = GET,
        path = "/:name"
    )
    def value(name: String): Future[Long]

    @endpoint(
        method = PUT,
        path = "/:name/increment"
    )
    def increment(name: String): Future[Long]

    @endpoint(
        method = PUT,
        path = "/:name/decrement"
    )
    def decrement(name: String): Future[Long]
}