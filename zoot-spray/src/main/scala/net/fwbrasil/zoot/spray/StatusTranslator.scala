package net.fwbrasil.zoot.spray

import net.fwbrasil.zoot.core.response.ResponseStatus
import spray.http.StatusCodes
import spray.http.StatusCode

object StatusTranslator {

    private val mapping = Map[ResponseStatus, StatusCode](
        ResponseStatus.CONTINUE -> StatusCodes.Continue,
        ResponseStatus.SWITCHING_PROTOCOLS -> StatusCodes.SwitchingProtocols,
        ResponseStatus.PROCESSING -> StatusCodes.Processing,
        ResponseStatus.OK -> StatusCodes.OK,
        ResponseStatus.CREATED -> StatusCodes.Created,
        ResponseStatus.ACCEPTED -> StatusCodes.Accepted,
        ResponseStatus.NON_AUTHORITATIVE_INFORMATION -> StatusCodes.NonAuthoritativeInformation,
        ResponseStatus.NO_CONTENT -> StatusCodes.NoContent,
        ResponseStatus.RESET_CONTENT -> StatusCodes.ResetContent,
        ResponseStatus.PARTIAL_CONTENT -> StatusCodes.PartialContent,
        ResponseStatus.MULTI_STATUS -> StatusCodes.MultiStatus,
        ResponseStatus.MULTIPLE_CHOICES -> StatusCodes.MultipleChoices,
        ResponseStatus.MOVED_PERMANENTLY -> StatusCodes.MovedPermanently,
        ResponseStatus.FOUND -> StatusCodes.Found,
        ResponseStatus.SEE_OTHER -> StatusCodes.SeeOther,
        ResponseStatus.NOT_MODIFIED -> StatusCodes.NotModified,
        ResponseStatus.USE_PROXY -> StatusCodes.UseProxy,
        ResponseStatus.TEMPORARY_REDIRECT -> StatusCodes.TemporaryRedirect,
        ResponseStatus.BAD_REQUEST -> StatusCodes.BadRequest,
        ResponseStatus.UNAUTHORIZED -> StatusCodes.Unauthorized,
        ResponseStatus.PAYMENT_REQUIRED -> StatusCodes.PaymentRequired,
        ResponseStatus.FORBIDDEN -> StatusCodes.Forbidden,
        ResponseStatus.NOT_FOUND -> StatusCodes.NotFound,
        ResponseStatus.METHOD_NOT_ALLOWED -> StatusCodes.MethodNotAllowed,
        ResponseStatus.NOT_ACCEPTABLE -> StatusCodes.NotAcceptable,
        ResponseStatus.PROXY_AUTHENTICATION_REQUIRED -> StatusCodes.ProxyAuthenticationRequired,
        ResponseStatus.REQUEST_TIMEOUT -> StatusCodes.RequestTimeout,
        ResponseStatus.CONFLICT -> StatusCodes.Conflict,
        ResponseStatus.GONE -> StatusCodes.Gone,
        ResponseStatus.LENGTH_REQUIRED -> StatusCodes.LengthRequired,
        ResponseStatus.PRECONDITION_FAILED -> StatusCodes.PreconditionFailed,
        ResponseStatus.REQUEST_ENTITY_TOO_LARGE -> StatusCodes.RequestEntityTooLarge,
        ResponseStatus.REQUEST_URI_TOO_LONG -> StatusCodes.RequestUriTooLong,
        ResponseStatus.UNSUPPORTED_MEDIA_TYPE -> StatusCodes.UnsupportedMediaType,
        ResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE -> StatusCodes.RequestedRangeNotSatisfiable,
        ResponseStatus.EXPECTATION_FAILED -> StatusCodes.ExpectationFailed,
        ResponseStatus.UNPROCESSABLE_ENTITY -> StatusCodes.UnprocessableEntity,
        ResponseStatus.LOCKED -> StatusCodes.Locked,
        ResponseStatus.FAILED_DEPENDENCY -> StatusCodes.FailedDependency,
        ResponseStatus.UNORDERED_COLLECTION -> StatusCodes.UnorderedCollection,
        ResponseStatus.UPGRADE_REQUIRED -> StatusCodes.UpgradeRequired,
        ResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE -> StatusCodes.RequestHeaderFieldsTooLarge,
        ResponseStatus.INTERNAL_SERVER_ERROR -> StatusCodes.InternalServerError,
        ResponseStatus.NOT_IMPLEMENTED -> StatusCodes.NotImplemented,
        ResponseStatus.BAD_GATEWAY -> StatusCodes.BadGateway,
        ResponseStatus.SERVICE_UNAVAILABLE -> StatusCodes.ServiceUnavailable,
        ResponseStatus.GATEWAY_TIMEOUT -> StatusCodes.GatewayTimeout,
        ResponseStatus.HTTP_VERSION_NOT_SUPPORTED -> StatusCodes.HTTPVersionNotSupported,
        ResponseStatus.VARIANT_ALSO_NEGOTIATES -> StatusCodes.VariantAlsoNegotiates,
        ResponseStatus.INSUFFICIENT_STORAGE -> StatusCodes.InsufficientStorage,
        ResponseStatus.NOT_EXTENDED -> StatusCodes.NotExtended
    )

    private val reverseMapping = mapping.map(_.swap)

    def fromHttpStatus(status: StatusCode) =
        reverseMapping(status)

    def toHttpStatus(status: ResponseStatus) =
        mapping(status)
}