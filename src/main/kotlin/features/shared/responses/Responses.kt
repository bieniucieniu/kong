package com.bieniucieniu.features.shared.responses

import ai.koog.prompt.streaming.StreamFrame
import com.bieniucieniu.features.shared.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow

suspend fun RoutingCall.notFound(message: String = "Not found") = respond(
    HttpStatusCode.NotFound,
    ErrorResponse(message)
)

suspend fun RoutingCall.unauthorized(message: String = "Not authorized") = respond(
    HttpStatusCode.Unauthorized,
    ErrorResponse(message)
)

suspend fun RoutingCall.badRequest(message: String = "Bad request") = respond(
    HttpStatusCode.BadRequest,
    ErrorResponse(message)
)

suspend fun RoutingCall.serviceUnavailable(message: String = "service unavailable") = respond(
    HttpStatusCode.ServiceUnavailable,
    ErrorResponse(message)
)

suspend fun RoutingCall.noContent(message: String) = respond(HttpStatusCode.NoContent, ErrorResponse(message))
suspend inline fun <reified T : Any> RoutingCall.noContent(body: T) = respond(HttpStatusCode.NoContent, body)


suspend fun RoutingCall.streamFlow(
    f: Flow<StreamFrame>,
    contentType: ContentType = ContentType.Text.EventStream,
    minChunkSize: Int = 50,
    onFlush: (String) -> Unit = {}
) {
    respondBytesWriter(contentType = contentType) {
        try {
            var acc = " "
            val writeAcc = suspend {
                writeByteArray(acc.toByteArray())
                onFlush(acc)
                flush()
                acc = ""
            }
            writeAcc()
            f.collect { chunk ->
                val str = when (chunk) {
                    is StreamFrame.Append -> chunk.text
                    else -> ""
                }
                acc += str
                if (acc.length > minChunkSize) writeAcc()
            }
            writeAcc()
        } catch (e: Throwable) {
            writeByteArray("error: ${e.message}".toByteArray())
        }
    }
}
