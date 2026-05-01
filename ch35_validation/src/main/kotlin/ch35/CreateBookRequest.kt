package ch35
import jakarta.validation.constraints.*

data class CreateBookRequest(
    @field:NotBlank(message = "Title required")
    val title: String = "",

    @field:NotBlank(message = "Author required")
    val author: String = "",

    @field:Pattern(
        regexp = "\\d{13}",
        message = "ISBN must be 13 digits"
    )
    val isbn: String = "",

    @field:Min(1450)
    @field:Max(2100)
    val year: Int = 0
)
