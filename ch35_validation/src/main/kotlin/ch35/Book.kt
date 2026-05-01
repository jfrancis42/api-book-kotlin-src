package ch35

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val year: Int,
    val available: Boolean = true
)
