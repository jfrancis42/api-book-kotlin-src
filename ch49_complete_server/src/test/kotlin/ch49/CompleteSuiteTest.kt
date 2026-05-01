package ch49

import ch49.model.Author
import ch49.model.Book
import ch49.repository.AuthorRepository
import ch49.repository.BookRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation
    .Autowired
import org.springframework.boot.test.autoconfigure
    .web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context
    .SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context
    .support.WithMockUser
import org.springframework.test.annotation
    .DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig::class)
@DirtiesContext(
    classMode = DirtiesContext.ClassMode
        .AFTER_EACH_TEST_METHOD
)
class CompleteSuiteTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var bookRepo: BookRepository
    @Autowired
    lateinit var authorRepo: AuthorRepository

    @BeforeEach
    fun setup() {
        val author = authorRepo.save(
            Author(name = "Test Author", bio = "")
        )
        bookRepo.save(
            Book(
                title = "Test Book",
                author = author,
                isbn = "1234567890123",
                year = 2020,
                available = true
            )
        )
    }

    @Test
    fun `GET books returns seeded data`() {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(
                jsonPath(
                    "$.content[0].title"
                ).value("Test Book")
            )
    }

    @Test
    @WithMockUser(roles = ["LIBRARIAN"])
    fun `full CRUD lifecycle`() {
        val authorBody = """
            {"name":"New Author","bio":"Bio text"}
        """.trimIndent()
        val authorResult = mockMvc.perform(
            post("/api/authors")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(authorBody)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val authorId = com.fasterxml.jackson
            .databind.ObjectMapper()
            .readTree(
                authorResult.response
                    .contentAsString
            )["id"].asLong()

        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content("""
                    {"title":"New Book",
                     "author_id":$authorId,
                     "isbn":"9876543210987",
                     "year":2024}
                """.trimIndent())
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `context loads and health is UP`() {
        mockMvc.perform(
            get("/actuator/health")
        )
            .andExpect(status().isOk)
    }
}
