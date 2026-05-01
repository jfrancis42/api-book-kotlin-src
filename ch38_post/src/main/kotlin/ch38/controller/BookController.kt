package ch38.controller
import ch38.dto.BookDto
import ch38.dto.CreateBookRequest
import ch38.service.BookService
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
}
