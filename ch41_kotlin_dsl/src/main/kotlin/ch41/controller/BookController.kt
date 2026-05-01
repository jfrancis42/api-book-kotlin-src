package ch41.controller

import ch41.dto.BookDto
import ch41.dto.CreateBookRequest
import ch41.service.BookService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    @GetMapping
    fun listBooks(): List<BookDto> =
        bookService.findAll()

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
