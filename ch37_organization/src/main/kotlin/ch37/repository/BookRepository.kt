package ch37.repository
import ch37.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
    fun findByAvailable(
        available: Boolean
    ): List<Book>
    fun findByTitleContainingIgnoreCase(
        title: String
    ): List<Book>
    fun existsByIsbn(isbn: String): Boolean
}
