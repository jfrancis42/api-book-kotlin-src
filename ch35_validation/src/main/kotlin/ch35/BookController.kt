package ch35
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/api/books")
class BookController {
    private val books =
        mutableListOf<Book>()
    private var nextId = 1L

    @GetMapping
    fun listBooks(): List<Book> = books

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long
    ): ResponseEntity<Book> =
        books.find { it.id == id }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun createBook(
        @Valid @RequestBody
        req: CreateBookRequest
    ): ResponseEntity<Book> {
        val book = Book(
            id = nextId++,
            title = req.title,
            author = req.author,
            isbn = req.isbn,
            year = req.year
        )
        books.add(book)
        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(book.id)
            .toUri()
        return ResponseEntity
            .created(location)
            .body(book)
    }
}
