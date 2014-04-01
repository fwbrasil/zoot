package net.fwbrasil.zoot.core.request

case class Request(
    path: String,
    method: RequestMethod = RequestMethod.GET,
    params: Map[String, String] = Map(),
    headers: Map[String, String] = Map()) {

    val requestPath = RequestPath(path)
}