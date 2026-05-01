package ch39
import ch39.dto.BookDto
import ch39.dto.UpdateBookRequest
import ch39.service.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ch39.controller.BookController::class)
class BookControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockBean lateinit var bookService: BookService

    @Test
    fun `PATCH returns 200 for partial update`() {
        val dto = BookDto(
            id = 1L,
            title = "New Title",
            authorName = "Martin",
            isbn = "9780132350884",
            year = 2008,
            available = true
        )
        whenever(
            bookService.update(eq(1L), any())
        ).thenReturn(dto)

        mockMvc.perform(
            patch("/api/books/1")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    """{"title": "New Title"}"""
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.title")
                    .value("New Title")
            )
    }

    @Test
    fun `DELETE returns 204`() {
        whenever(bookService.delete(eq(1L)))
            .thenReturn(true)

        mockMvc.perform(delete("/api/books/1"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `DELETE returns 404 for missing book`() {
        whenever(bookService.delete(eq(99L)))
            .thenReturn(false)

        mockMvc.perform(delete("/api/books/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `PUT returns 200 with updated book`() {
        val dto = BookDto(
            id = 1L,
            title = "Clean Code 2e",
            authorName = "Martin",
            isbn = "9780132350884",
            year = 2008,
            available = true
        )
        whenever(
            bookService.update(eq(1L), any())
        ).thenReturn(dto)

        val body = mapOf(
            "title" to "Clean Code 2e",
            "isbn" to "9780132350884",
            "year" to 2008,
            "available" to true
        )

        mockMvc.perform(
            put("/api/books/1")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    objectMapper
                        .writeValueAsString(body)
                )
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.title")
                    .value("Clean Code 2e")
            )
    }
}
