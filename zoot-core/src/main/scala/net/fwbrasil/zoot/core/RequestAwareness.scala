package net.fwbrasil.zoot.core

import net.fwbrasil.smirror.SMethod
import net.fwbrasil.zoot.core.request.Request

trait RequestAwareness extends Api {

    private val currentRequestThreadLocal = new ThreadLocal[Option[Request]] {
        override def initialValue = None
    }

    protected def withRequest[T](action: Request => T) = {
        try action(currentRequest)
        finally currentRequestThreadLocal.remove
    }

    override def action(request: Request, sMethod: SMethod[Any], params: Any*) = {
        currentRequestThreadLocal.set(Some(request))
        try super.action(request, sMethod, params: _*)
        finally currentRequestThreadLocal.remove()
    }

    private def currentRequest =
        currentRequestThreadLocal.get.getOrElse(throw new IllegalStateException("Do not use 'withRequest' in a future."))
}