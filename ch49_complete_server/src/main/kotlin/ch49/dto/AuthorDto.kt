package ch49.dto

import ch49.model.Author
import jakarta.validation.constraints.NotBlank

data class AuthorDto(
    val id: Long = 0,
    val name: String = "",
    val bio: String = ""
) {
    companion object {
        fun from(author: Author) = AuthorDto(
            id = author.id,
            name = author.name,
            bio = author.bio
        )
    }
}

data class CreateAuthorRequest(
    @field:NotBlank(
        message = "must not be blank"
    )
    val name: String = "",
    val bio: String = ""
)
