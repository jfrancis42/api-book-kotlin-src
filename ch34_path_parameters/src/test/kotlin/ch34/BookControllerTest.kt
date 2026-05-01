package ch34
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
    fun `filter by available`() {
        mockMvc.perform(
            get("/api/books?available=true")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()")
                .value(1))
    }

    @Test
    fun `search by title`() {
        mockMvc.perform(
            get("/api/books?q=kotlin")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()")
                .value(1))
    }

    @Test
    fun `get by id 404`() {
        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound)
    }
}
