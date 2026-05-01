package ch47

import ch47.model.Author
import ch47.model.Book
import ch47.repository.BookRepository
import ch47.repository.BookSpecifications
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation
    .Autowired
import org.springframework.boot.test.autoconfigure
    .orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure
    .orm.jpa.TestEntityManager

@DataJpaTest
class BookRepositoryDataJpaTest {
    @Autowired
    lateinit var em: TestEntityManager
    @Autowired
    lateinit var repo: BookRepository

    @Test
    fun `save and find book`() {
        val author = em.persistAndFlush(
            Author(name = "Test Author", bio = "")
        )
        val book = em.persistAndFlush(
            Book(
                title = "Test Book",
                author = author,
                isbn = "1234567890123",
                year = 2020,
                available = true
            )
        )
        val found = repo.findById(book.id)
        assertTrue(found.isPresent)
        assertEquals(
            "Test Book", found.get().title
        )
    }

    @Test
    fun `specification finds by title`() {
        val author = em.persistAndFlush(
            Author(name = "Alice", bio = "")
        )
        em.persistAndFlush(
            Book(
                title = "Kotlin Guide",
                author = author,
                isbn = "9781234567890",
                year = 2021,
                available = true
            )
        )
        val results = repo.findAll(
            BookSpecifications.titleContains(
                "kotlin"
            )
        )
        assertEquals(1, results.size)
    }
}
