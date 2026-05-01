package ch33
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `GET books returns 200`() {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(
                content().contentTypeCompatibleWith(
                    "application/json"
                )
            )
    }

    @Test
    fun `GET book by id returns 200`() {
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.title")
                    .value("Kotlin in Action")
            )
    }

    @Test
    fun `GET unknown id returns 404`() {
        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound)
    }
}
