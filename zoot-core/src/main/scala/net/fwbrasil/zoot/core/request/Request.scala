package net.fwbrasil.zoot.core.request

case class Request(
    method: RequestMethod,
    path: String,
    params: Map[String, String] = Map(),
    headers: Map[String, String] = Map()) {

    val requestPath = RequestPath(path)
}