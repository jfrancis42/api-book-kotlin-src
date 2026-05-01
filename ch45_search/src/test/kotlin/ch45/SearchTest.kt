package ch45

import ch45.model.Author
import ch45.model.Book
import ch45.repository.BookRepository
import ch45.repository.BookSpecifications
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SearchTest {
    @Autowired
    lateinit var em: TestEntityManager
    @Autowired
    lateinit var repo: BookRepository

    private lateinit var alice: Author

    @BeforeEach
    fun setup() {
        alice = em.persistAndFlush(
            Author(name = "Alice Ko", bio = "")
        )
        em.persistAndFlush(
            Book(
                title = "Kotlin in Action",
                author = alice,
                isbn = "9781617293290",
                year = 2017,
                available = true
            )
        )
        em.persistAndFlush(
            Book(
                title = "Spring Boot Guide",
                author = alice,
                isbn = "9781617294945",
                year = 2021,
                available = false
            )
        )
    }

    @Test
    fun `title search finds matching books`() {
        val spec = BookSpecifications
            .titleContains("kotlin")
        val results = repo.findAll(spec)
        assertEquals(1, results.size)
        assertEquals(
            "Kotlin in Action",
            results[0].title
        )
    }

    @Test
    fun `available filter excludes unavailable`() {
        val results = repo.findAll(
            BookSpecifications.isAvailable()
        )
        assertEquals(1, results.size)
        assertTrue(results[0].available)
    }

    @Test
    fun `author name search works`() {
        val spec = BookSpecifications
            .authorNameContains("Alice")
        val results = repo.findAll(spec)
        assertEquals(2, results.size)
    }
}
