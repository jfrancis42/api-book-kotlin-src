package ch48.controller

import ch48.dto.BookDto
import ch48.dto.CreateBookRequest
import ch48.service.BookService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
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
    ): Page<BookDto> =
        bookService.findAll(pageable)

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long
    ): ResponseEntity<BookDto> {
        val book = bookService.findById(id)
            ?: return ResponseEntity
                .notFound().build()
        return ResponseEntity.ok(book)
    }

    @PostMapping
    fun createBook(
        @Valid
        @RequestBody req: CreateBookRequest
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
