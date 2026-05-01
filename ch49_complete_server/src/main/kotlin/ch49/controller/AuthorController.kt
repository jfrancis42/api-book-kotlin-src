package ch49.controller

import ch49.dto.AuthorDto
import ch49.dto.CreateAuthorRequest
import ch49.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    @GetMapping
    fun listAuthors(): List<AuthorDto> =
        authorService.findAll()

    @GetMapping("/{id}")
    fun getAuthor(
        @PathVariable id: Long
    ): AuthorDto = authorService.findById(id)

    @PostMapping
    fun createAuthor(
        @Valid
        @RequestBody req: CreateAuthorRequest
    ): ResponseEntity<AuthorDto> {
        val author = authorService.create(req)
        return ResponseEntity.status(201)
            .body(author)
    }
}
