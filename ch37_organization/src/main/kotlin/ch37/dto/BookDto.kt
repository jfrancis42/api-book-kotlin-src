package ch37.dto

data class BookDto(
    val id: Long,
    val title: String,
    val authorName: String?,
    val isbn: String,
    val year: Int,
    val available: Boolean
)
