package com.bieniucieniu.features.shared.models

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Paginated<T>(
    val data: List<T>,
    val offset: Long,
    val count: Int,
    val end: Boolean
)

fun RoutingContext.pagination(
    defaultOffset: Long = 0,
    defaultCount: Int = 40
) = Pagination(
    call.parameters["offset"]?.toLongOrNull() ?: defaultOffset,
    call.parameters["count"]?.toIntOrNull() ?: defaultCount
)

@Serializable
data class Pagination(
    val offset: Long,
    val count: Int,
) {
    fun <T> paginated(
        data: List<T>,
        offset: Long = this.offset,
        count: Int = this.count,
        end: Boolean = data.size != count
    ) = Paginated(data, offset, count, end)
}