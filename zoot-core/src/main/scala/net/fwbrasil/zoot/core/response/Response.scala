package net.fwbrasil.zoot.core.response

sealed trait Response[T] {
    val status: ResponseStatus
    val headers: Map[String, String]
    val body: T
    
    def withBody[U](body: U): Response[U]
}

object Response {
    def apply[T](
        body: T = "",
        status: ResponseStatus = ResponseStatus.OK,
        headers: Map[String, String] = Map()): Response[T] =
        NormalResponse(body, status, headers)
}

case class NormalResponse[T](body: T = "",
                             status: ResponseStatus = ResponseStatus.OK,
                             headers: Map[String, String] = Map())
    extends Response[T] {
    
    def withBody[U](body: U) =
        this.copy(body = body)
}

case class ExceptionResponse[T](body: T = "",
                                status: ResponseStatus = ResponseStatus.INTERNAL_SERVER_ERROR,
                                headers: Map[String, String] = Map())
    extends Exception
    with Response[T] {
    
    def withBody[U](body: U) =
        this.copy(body = body)
}