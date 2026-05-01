package ch37.controller
import ch37.dto.BookDto
import ch37.dto.CreateBookRequest
import ch37.service.BookService
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
        return ResponseEntity.ok(book)
    }
}
