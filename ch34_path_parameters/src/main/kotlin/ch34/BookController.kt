package ch34
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController {
    private val books = mutableListOf(
        Book(1, "Kotlin in Action",
             "Jemerov", "9781617293290",
             2017, true),
        Book(2, "Clean Code",
             "Martin", "9780132350884",
             2008, false)
    )

    @GetMapping
    fun listBooks(
        @RequestParam available: Boolean? = null,
        @RequestParam q: String? = null
    ): List<Book> = books.filter { book ->
        (available == null ||
            book.available == available) &&
        (q == null ||
            book.title.contains(q, true))
    }

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long
    ): ResponseEntity<Book> {
        val book = books.find { it.id == id }
            ?: return ResponseEntity
                .notFound().build()
        return ResponseEntity.ok(book)
    }
}
