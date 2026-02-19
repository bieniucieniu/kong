package com.bieniucieniu.errors.responses

import io.ktor.http.*
import io.ktor.util.reflect.*

open class ResponsesException(
    val status: HttpStatusCode,
    message: String = status.description,
    cause: Throwable? = null
) :
    Throwable(message, cause)

class ResponsesExceptionWithContent(
    val status: HttpStatusCode,
    message: String = status.description,
    val content: Any,
    val typeInfo: TypeInfo,
    cause: Throwable? = null
) : Throwable(message, cause)


fun notFound(message: String = "Not found") = ResponsesException(HttpStatusCode.NotFound, message)
fun unauthorized(message: String = "Not authorized") = ResponsesException(HttpStatusCode.Unauthorized, message)
fun badRequest(message: String = "Bad request") = ResponsesException(HttpStatusCode.BadRequest, message)
fun serviceUnavailable(message: String = "service unavailable") =
    ResponsesException(HttpStatusCode.ServiceUnavailable, message)

fun noContent(message: String) = ResponsesException(HttpStatusCode.NoContent, message)
inline fun <reified T> noContent(message: String, content: T) =
    ResponsesExceptionWithContent(HttpStatusCode.NoContent, message, content as Any, typeInfo<T>())
