package net.fwbrasil.zoot.finagle.response

import org.jboss.netty.handler.codec.http.HttpResponseStatus

import net.fwbrasil.zoot.core.response.ResponseStatus

object responseStatus {

    private val mapping = Map(
        ResponseStatus.CONTINUE -> HttpResponseStatus.CONTINUE,
        ResponseStatus.SWITCHING_PROTOCOLS -> HttpResponseStatus.SWITCHING_PROTOCOLS,
        ResponseStatus.PROCESSING -> HttpResponseStatus.PROCESSING,
        ResponseStatus.OK -> HttpResponseStatus.OK,
        ResponseStatus.CREATED -> HttpResponseStatus.CREATED,
        ResponseStatus.ACCEPTED -> HttpResponseStatus.ACCEPTED,
        ResponseStatus.NON_AUTHORITATIVE_INFORMATION -> HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION,
        ResponseStatus.NO_CONTENT -> HttpResponseStatus.NO_CONTENT,
        ResponseStatus.RESET_CONTENT -> HttpResponseStatus.RESET_CONTENT,
        ResponseStatus.PARTIAL_CONTENT -> HttpResponseStatus.PARTIAL_CONTENT,
        ResponseStatus.MULTI_STATUS -> HttpResponseStatus.MULTI_STATUS,
        ResponseStatus.MULTIPLE_CHOICES -> HttpResponseStatus.MULTIPLE_CHOICES,
        ResponseStatus.MOVED_PERMANENTLY -> HttpResponseStatus.MOVED_PERMANENTLY,
        ResponseStatus.FOUND -> HttpResponseStatus.FOUND,
        ResponseStatus.SEE_OTHER -> HttpResponseStatus.SEE_OTHER,
        ResponseStatus.NOT_MODIFIED -> HttpResponseStatus.NOT_MODIFIED,
        ResponseStatus.USE_PROXY -> HttpResponseStatus.USE_PROXY,
        ResponseStatus.TEMPORARY_REDIRECT -> HttpResponseStatus.TEMPORARY_REDIRECT,
        ResponseStatus.BAD_REQUEST -> HttpResponseStatus.BAD_REQUEST,
        ResponseStatus.UNAUTHORIZED -> HttpResponseStatus.UNAUTHORIZED,
        ResponseStatus.PAYMENT_REQUIRED -> HttpResponseStatus.PAYMENT_REQUIRED,
        ResponseStatus.FORBIDDEN -> HttpResponseStatus.FORBIDDEN,
        ResponseStatus.NOT_FOUND -> HttpResponseStatus.NOT_FOUND,
        ResponseStatus.METHOD_NOT_ALLOWED -> HttpResponseStatus.METHOD_NOT_ALLOWED,
        ResponseStatus.NOT_ACCEPTABLE -> HttpResponseStatus.NOT_ACCEPTABLE,
        ResponseStatus.PROXY_AUTHENTICATION_REQUIRED -> HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED,
        ResponseStatus.REQUEST_TIMEOUT -> HttpResponseStatus.REQUEST_TIMEOUT,
        ResponseStatus.CONFLICT -> HttpResponseStatus.CONFLICT,
        ResponseStatus.GONE -> HttpResponseStatus.GONE,
        ResponseStatus.LENGTH_REQUIRED -> HttpResponseStatus.LENGTH_REQUIRED,
        ResponseStatus.PRECONDITION_FAILED -> HttpResponseStatus.PRECONDITION_FAILED,
        ResponseStatus.REQUEST_ENTITY_TOO_LARGE -> HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE,
        ResponseStatus.REQUEST_URI_TOO_LONG -> HttpResponseStatus.REQUEST_URI_TOO_LONG,
        ResponseStatus.UNSUPPORTED_MEDIA_TYPE -> HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE,
        ResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE -> HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
        ResponseStatus.EXPECTATION_FAILED -> HttpResponseStatus.EXPECTATION_FAILED,
        ResponseStatus.UNPROCESSABLE_ENTITY -> HttpResponseStatus.UNPROCESSABLE_ENTITY,
        ResponseStatus.LOCKED -> HttpResponseStatus.LOCKED,
        ResponseStatus.FAILED_DEPENDENCY -> HttpResponseStatus.FAILED_DEPENDENCY,
        ResponseStatus.UNORDERED_COLLECTION -> HttpResponseStatus.UNORDERED_COLLECTION,
        ResponseStatus.UPGRADE_REQUIRED -> HttpResponseStatus.UPGRADE_REQUIRED,
        ResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE -> HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE,
        ResponseStatus.INTERNAL_SERVER_ERROR -> HttpResponseStatus.INTERNAL_SERVER_ERROR,
        ResponseStatus.NOT_IMPLEMENTED -> HttpResponseStatus.NOT_IMPLEMENTED,
        ResponseStatus.BAD_GATEWAY -> HttpResponseStatus.BAD_GATEWAY,
        ResponseStatus.SERVICE_UNAVAILABLE -> HttpResponseStatus.SERVICE_UNAVAILABLE,
        ResponseStatus.GATEWAY_TIMEOUT -> HttpResponseStatus.GATEWAY_TIMEOUT,
        ResponseStatus.HTTP_VERSION_NOT_SUPPORTED -> HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED,
        ResponseStatus.VARIANT_ALSO_NEGOTIATES -> HttpResponseStatus.VARIANT_ALSO_NEGOTIATES,
        ResponseStatus.INSUFFICIENT_STORAGE -> HttpResponseStatus.INSUFFICIENT_STORAGE,
        ResponseStatus.NOT_EXTENDED -> HttpResponseStatus.NOT_EXTENDED
    )

    private val reverseMapping = mapping.map(_.swap)

    def fromFinagle(status: HttpResponseStatus) =
        reverseMapping(status)

    def toFinagle(status: ResponseStatus) =
        mapping(status)
}