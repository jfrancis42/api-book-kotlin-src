package ch49.dto

import ch49.model.Book
import jakarta.validation.constraints.NotBlank

data class BookDto(
    val id: Long = 0,
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val year: Int = 0,
    val available: Boolean = true,
    val createdAt: java.time.Instant? = null,
    val updatedAt: java.time.Instant? = null
) {
    companion object {
        fun from(book: Book) = BookDto(
            id = book.id,
            title = book.title,
            author = book.author?.name ?: "",
            isbn = book.isbn,
            year = book.year,
            available = book.available,
            createdAt = book.createdAt,
            updatedAt = book.updatedAt
        )
    }
}

data class CreateBookRequest(
    @field:NotBlank(
        message = "must not be blank"
    )
    val title: String = "",
    val author_id: Long? = null,
    val isbn: String = "",
    val year: Int = 0
)

data class UpdateBookRequest(
    val title: String? = null,
    val isbn: String? = null,
    val year: Int? = null
)
