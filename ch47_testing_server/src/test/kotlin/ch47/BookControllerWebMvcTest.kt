package ch47

import ch47.config.SecurityConfig
import ch47.controller.BookController
import ch47.dto.BookDto
import ch47.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation
    .Autowired
import org.springframework.boot.test.autoconfigure
    .web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito
    .MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
@Import(SecurityConfig::class,
    TestSecurityConfig::class)
class BookControllerWebMvcTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @MockBean
    lateinit var bookService: BookService

    @Test
    fun `list books returns 200`() {
        whenever(bookService.findAll(any()))
            .thenReturn(PageImpl(listOf(
                BookDto(1, "Kotlin in Action",
                    "Jemerov",
                    "9781617293290", 2017, true)
            )))
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(
                jsonPath(
                    "$.content[0].title"
                ).value("Kotlin in Action")
            )
    }

    @Test
    fun `get unknown book returns 404`() {
        whenever(bookService.findById(99L))
            .thenReturn(null)
        mockMvc.perform(get("/api/books/99"))
            .andExpect(status().isNotFound)
    }
}
