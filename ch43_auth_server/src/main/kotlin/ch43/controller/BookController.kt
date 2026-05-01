package ch43.controller

import ch43.dto.BookDto
import ch43.dto.CreateBookRequest
import ch43.dto.PagedResponse
import ch43.service.BookService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping
    fun listBooks(
        @PageableDefault(size = 20)
        pageable: Pageable
    ): PagedResponse<BookDto> =
        PagedResponse.from(
            bookService.findAll(pageable)
        )

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long
    ): BookDto = bookService.findById(id)

    @PostMapping
    fun createBook(
        @Valid @RequestBody req: CreateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.create(req)
        return ResponseEntity.status(201).body(book)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        bookService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
