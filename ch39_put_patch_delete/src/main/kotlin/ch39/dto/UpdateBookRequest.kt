package ch39.dto
data class UpdateBookRequest(
    val title: String? = null,
    val isbn: String? = null,
    val year: Int? = null,
    val available: Boolean? = null
)
