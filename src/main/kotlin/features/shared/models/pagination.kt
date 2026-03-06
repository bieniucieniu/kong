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
    defaultCount: Int = 40,
): Pagination {

    return Pagination(
        call.parameters["offset"]?.toLongOrNull() ?: defaultOffset,
        call.parameters["count"]?.toIntOrNull() ?: defaultCount
    )
}

@Serializable
data class Pagination(
    val offset: Long,
    val count: Int,
) {
    fun <T> toPaginatedWith(
        data: List<T>,
        offset: Long = this.offset,
        count: Int = this.count,
        end: Boolean = data.size != count
    ) = Paginated(data, offset, count, end)
}

fun <T> List<T>.toPaginated(
    p: Pagination
) = p.toPaginatedWith(this)

fun <T> List<T>.toPaginated(
    offset: Long,
    count: Int,
    end: Boolean = this.size != count
) = Paginated(this, offset, count, end)
