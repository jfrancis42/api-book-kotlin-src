package ch36
import ch36.model.Author
import ch36.model.Book
import ch36.repository.BookRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class BookRepositoryTest {
    @Autowired lateinit var em: TestEntityManager
    @Autowired lateinit var repo: BookRepository

    @Test
    fun `findByAvailable returns only available`() {
        val author = em.persistAndFlush(
            Author(name = "Alice", bio = "")
        )
        em.persistAndFlush(
            Book(title = "A", author = author,
                isbn = "1111111111111",
                year = 2020, available = true)
        )
        em.persistAndFlush(
            Book(title = "B", author = author,
                isbn = "2222222222222",
                year = 2021, available = false)
        )
        val available = repo.findByAvailable(true)
        assertEquals(1, available.size)
        assertEquals("A", available[0].title)
    }

    @Test
    fun `existsByIsbn finds existing ISBN`() {
        val author = em.persistAndFlush(
            Author(name = "Bob", bio = "")
        )
        em.persistAndFlush(
            Book(title = "C", author = author,
                isbn = "3333333333333",
                year = 2022, available = true)
        )
        assertTrue(
            repo.existsByIsbn("3333333333333")
        )
        assertFalse(
            repo.existsByIsbn("9999999999999")
        )
    }

    @Test
    fun `findByTitleContaining is case insensitive`() {
        val author = em.persistAndFlush(
            Author(name = "Carol", bio = "")
        )
        em.persistAndFlush(
            Book(title = "Kotlin Guide",
                author = author,
                isbn = "4444444444444",
                year = 2023, available = true)
        )
        val results =
            repo.findByTitleContainingIgnoreCase(
                "kotlin"
            )
        assertEquals(1, results.size)
    }
}
