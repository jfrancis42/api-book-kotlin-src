package ch33
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController {
    private val books = mutableListOf(
        Book(1, "Kotlin in Action",
             "Jemerov", "9781617293290",
             2017),
        Book(2, "Effective Kotlin",
             "Moskala", "9788395452840",
             2021)
    )

    @GetMapping
    fun listBooks(): List<Book> = books

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
