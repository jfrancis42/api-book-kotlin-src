package ch49.service

import ch49.dto.AuthorDto
import ch49.dto.CreateAuthorRequest
import ch49.exception.LibraryException
import ch49.model.Author
import ch49.repository.AuthorRepository
import org.springframework.stereotype.Service

@Service
class AuthorService(
    private val authorRepository: AuthorRepository
) {
    fun findAll(): List<AuthorDto> =
        authorRepository.findAll()
            .map { AuthorDto.from(it) }

    fun findById(id: Long): AuthorDto =
        authorRepository.findById(id)
            .map { AuthorDto.from(it) }
            .orElseThrow {
                LibraryException
                    .AuthorNotFoundException(id)
            }

    fun create(
        req: CreateAuthorRequest
    ): AuthorDto {
        val author = Author(
            name = req.name,
            bio = req.bio
        )
        return AuthorDto.from(
            authorRepository.save(author)
        )
    }
}
