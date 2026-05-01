package ch39.controller
import ch39.dto.BookDto
import ch39.dto.CreateBookRequest
import ch39.dto.UpdateBookRequest
import ch39.service.BookService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support
    .ServletUriComponentsBuilder

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
    ): ResponseEntity<BookDto> {
        val book = bookService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(book)
    }

    @PostMapping
    fun createBook(
        @Valid @RequestBody
        request: CreateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.create(request)
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(book.id)
                .toUri()
        return ResponseEntity
            .created(location)
            .body(book)
    }

    @PatchMapping("/{id}")
    fun patchBook(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.update(id, request)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(book)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.update(id, request)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(book)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val deleted = bookService.delete(id)
        if (!deleted)
            return ResponseEntity.notFound().build()
        return ResponseEntity.noContent().build()
    }
}
