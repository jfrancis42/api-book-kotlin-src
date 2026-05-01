package ch40

import ch40.controller.BookController
import ch40.exception.LibraryException
import ch40.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class GlobalExceptionHandlerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var bookService: BookService

    @Test
    fun `missing book returns 404 with code`() {
        whenever(bookService.findById(99L))
            .thenThrow(
                LibraryException
                    .BookNotFoundException(99L)
            )

        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound)
            .andExpect(
                jsonPath("$.code")
                    .value("NOT_FOUND")
            )
            .andExpect(
                jsonPath("$.status").value(404)
            )
    }

    @Test
    fun `validation error returns 400 with details`() {
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content("""{"title":""}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(
                jsonPath("$.code")
                    .value("VALIDATION_ERROR")
            )
            .andExpect(
                jsonPath("$.details").isArray
            )
    }
}
