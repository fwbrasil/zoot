package net.fwbrasil.zoot.core

import net.fwbrasil.zoot.core.api.EndpointAnnotation
import net.fwbrasil.smirror.SMethod
import net.fwbrasil.zoot.core.request.Request
import scala.concurrent.Future

trait Api {

    protected type endpoint = EndpointAnnotation

    private[zoot] def exec(request: Request, sMethod: SMethod[Any], params: Any*) =
        action(request, sMethod, params: _*)

    protected def action(request: Request, sMethod: SMethod[Any], params: Any*) =
        sMethod.invoke(this, params: _*).asInstanceOf[Future[Any]]
}