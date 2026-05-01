package ch38
import ch38.dto.BookDto
import ch38.service.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ch38.controller.BookController::class)
class BookControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockBean lateinit var bookService: BookService

    @Test
    fun `POST books returns 201 with Location`() {
        val dto = BookDto(
            id = 1, title = "Kotlin in Action",
            authorName = "Jemerov",
            isbn = "9781617293290",
            year = 2017, available = true
        )
        whenever(bookService.create(any()))
            .thenReturn(dto)

        val body = mapOf(
            "title" to "Kotlin in Action",
            "authorId" to 1,
            "isbn" to "9781617293290",
            "year" to 2017
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    objectMapper.writeValueAsString(
                        body
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(
                jsonPath("$.id").value(1)
            )
    }

    @Test
    fun `POST with blank title returns 400`() {
        val body = mapOf(
            "title" to "",
            "authorId" to 1,
            "isbn" to "9781617293290",
            "year" to 2017
        )
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    objectMapper.writeValueAsString(
                        body
                    )
                )
        )
            .andExpect(status().isBadRequest)
    }
}
