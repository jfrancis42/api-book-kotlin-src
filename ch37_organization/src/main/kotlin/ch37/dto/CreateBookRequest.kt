package ch37.dto
import jakarta.validation.constraints.*

data class CreateBookRequest(
    @field:NotBlank val title: String = "",
    @field:NotNull val authorId: Long = 0,
    @field:Pattern(regexp = "\\d{13}")
    val isbn: String = "",
    @field:Min(1450) @field:Max(2100)
    val year: Int = 0
)
