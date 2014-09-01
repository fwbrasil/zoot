package net.fwbrasil.zoot.core.endpoint

import net.fwbrasil.zoot.core.Encoder
import net.fwbrasil.zoot.core.request.Request

case class Session(header: String, param: String)

class SessionEncoder extends Encoder[Session] {
    def encode(value: Session, request: Request) =
        request.addHeaders(Map("header" -> value.header)).addParam("param", value.param)
    def decode(response: Request) = 
        Session(response.headers("header"), response.params("param"))
}