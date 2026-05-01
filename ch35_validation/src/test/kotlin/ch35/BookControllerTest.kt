package ch35
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `valid POST returns 201`() {
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content("""
                    {"title":"Kotlin in Action",
                     "author":"Jemerov",
                     "isbn":"9781617293290",
                     "year":2017}
                """.trimIndent())
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
    }

    @Test
    fun `blank title returns 400`() {
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content("""
                    {"title":"","author":"X",
                     "isbn":"9781617293290",
                     "year":2017}
                """.trimIndent())
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `invalid ISBN returns 400`() {
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content("""
                    {"title":"T","author":"A",
                     "isbn":"bad","year":2017}
                """.trimIndent())
        )
            .andExpect(status().isBadRequest)
    }
}
