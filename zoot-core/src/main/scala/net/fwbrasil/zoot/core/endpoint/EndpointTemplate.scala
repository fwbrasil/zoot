package net.fwbrasil.zoot.core.endpoint

import net.fwbrasil.zoot.core.request.Request
import net.fwbrasil.zoot.core.request.RequestMethod
import net.fwbrasil.zoot.core.request.RequestPath
import net.fwbrasil.zoot.core.request.RequestPathTemplate

case class EndpointTemplate private[EndpointTemplate] (
    method: String, path: RequestPathTemplate) {

    def tryParse(request: Request) =
        if (request.method == method)
            path.tryParse(request.requestPath)
        else
            None
}

object EndpointTemplate {
    
    def apply(method: String, path: String) =
        new EndpointTemplate(
            method,
            RequestPathTemplate(RequestPath(path)))
}