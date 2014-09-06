package net.fwbrasil.zoot.core.response

case class Response[T](body: T = "",
                  status: ResponseStatus = ResponseStatus.OK,
                  headers: Map[String, String] = Map())

case class ExceptionResponse(body: String = "",
                             status: ResponseStatus = ResponseStatus.INTERNAL_SERVER_ERROR,
                             headers: Map[String, String] = Map())
    extends Exception
    
