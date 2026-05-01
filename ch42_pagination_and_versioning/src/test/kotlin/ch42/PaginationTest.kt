package ch42

import ch42.controller.BookController
import ch42.dto.BookDto
import ch42.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
class PaginationTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var bookService: BookService

    @Test
    fun `paginated list returns correct shape`() {
        val books = listOf(
            BookDto(
                1, "Kotlin in Action",
                "Jemerov", "9781617293290",
                2017, true
            )
        )
        whenever(bookService.findAll(any()))
            .thenReturn(PageImpl(books))

        mockMvc.perform(
            get("/api/books?page=0&size=10")
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.content")
                    .isArray
            )
            .andExpect(
                jsonPath(
                    "$.total_elements"
                ).value(1)
            )
    }
}
