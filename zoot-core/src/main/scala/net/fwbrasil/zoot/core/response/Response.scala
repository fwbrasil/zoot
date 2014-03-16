package net.fwbrasil.zoot.core.response

sealed trait Response[T] {
    val status: ResponseStatus
    val headers: Map[String, String]
    val body: T
}

object Response {
    def apply[T](status: ResponseStatus = ResponseStatus.OK,
              body: T = "",
              headers: Map[String, String] = Map()): Response[T] =
        NormalResponse(status, body, headers)
}

case class NormalResponse[T](status: ResponseStatus = ResponseStatus.OK,
                         body: T = "",
                         headers: Map[String, String] = Map())
    extends Response[T]

case class ExceptionResponse[T](status: ResponseStatus = ResponseStatus.INTERNAL_SERVER_ERROR,
                             body: T = "",
                             headers: Map[String, String] = Map())
    extends Exception
    with Response[T]