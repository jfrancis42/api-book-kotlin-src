package ch40.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class BookDto(
    val id: Long = 0,
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val year: Int = 0,
    val available: Boolean = true
)

data class CreateBookRequest(
    @field:NotBlank(message = "must not be blank")
    val title: String = "",
    val author: String = "",
    @field:NotBlank(message = "must not be blank")
    val isbn: String = "",
    @field:Positive(message = "must be positive")
    val year: Int = 0
)
