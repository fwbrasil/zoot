package net.fwbrasil.zoot.core.request

case class Request(
    path: String,
    method: String = RequestMethod.GET,
    params: Map[String, String] = Map(),
    headers: Map[String, String] = Map(),
    originalRequest: Option[Any] = None) {

    val requestPath = RequestPath(path)
    val host = headers.get("Host")

    def addHeaders(headers: Map[String, String]) =
        this.copy(headers = this.headers ++ headers)
        
    def addParam(name: String, value: String) =
        this.copy(params = params + (name -> value))
}