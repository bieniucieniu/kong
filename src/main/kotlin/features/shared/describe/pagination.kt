import io.ktor.openapi.*

fun Parameters.Builder.paginationQueryParams() {
    query("offset") {
        description = "Offset"
        schema = jsonSchema<Long>()
        required = false
    }
    query("count") {
        description = "Count"
        schema = jsonSchema<Int>()
        required = false
    }
}