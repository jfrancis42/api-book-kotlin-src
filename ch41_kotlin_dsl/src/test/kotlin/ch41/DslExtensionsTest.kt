package ch41

import ch41.controller.BookController
import ch41.dsl.getJson
import ch41.dto.BookDto
import ch41.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class DslExtensionsTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var bookService: BookService

    @Test
    fun `getJson returns book list`() {
        whenever(bookService.findAll())
            .thenReturn(listOf(
                BookDto(
                    1, "Kotlin in Action",
                    "Jemerov",
                    "9781617293290", 2017, true
                )
            ))

        mockMvc.getJson("/api/books")
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$[0].title")
                    .value("Kotlin in Action")
            )
    }
}
