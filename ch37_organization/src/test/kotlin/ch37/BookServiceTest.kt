package ch37
import ch37.dto.CreateBookRequest
import ch37.model.Author
import ch37.model.Book
import ch37.repository.AuthorRepository
import ch37.repository.BookRepository
import ch37.service.BookService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.Optional

class BookServiceTest {
    private val bookRepo = mock<BookRepository>()
    private val authorRepo = mock<AuthorRepository>()
    private val service = BookService(
        bookRepo, authorRepo
    )

    @Test
    fun `findAll returns mapped DTOs`() {
        val author = Author(
            id = 1, name = "Alice", bio = ""
        )
        val book = Book(
            id = 1, title = "Kotlin",
            author = author,
            isbn = "1234567890123",
            year = 2021, available = true
        )
        whenever(bookRepo.findAll())
            .thenReturn(listOf(book))

        val result = service.findAll()

        assertEquals(1, result.size)
        assertEquals("Kotlin", result[0].title)
        assertEquals("Alice", result[0].authorName)
    }

    @Test
    fun `create throws when author not found`() {
        whenever(authorRepo.findById(99L))
            .thenReturn(Optional.empty())

        assertThrows(NoSuchElementException::class.java) {
            service.create(
                CreateBookRequest(
                    title = "X",
                    authorId = 99L,
                    isbn = "1234567890123",
                    year = 2021
                )
            )
        }
    }
}
