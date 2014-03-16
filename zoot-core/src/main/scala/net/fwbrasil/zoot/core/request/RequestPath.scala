package net.fwbrasil.zoot.core.request

case class RequestPath(elements: List[String]) {
    override def toString = elements.mkString(RequestPath.separator)
}

object RequestPath {
    val separator = "/"
    def apply(path: String): RequestPath =
            RequestPath(path.split(separator, -1).toList)
}