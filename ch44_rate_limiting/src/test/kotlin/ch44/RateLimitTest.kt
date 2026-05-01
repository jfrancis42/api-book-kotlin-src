package ch44

import ch44.config.SecurityConfig
import ch44.config.WebConfig
import ch44.controller.BookController
import ch44.dto.PagedResponse
import ch44.ratelimit.RateLimitInterceptor
import ch44.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
@Import(
    SecurityConfig::class,
    WebConfig::class,
    RateLimitInterceptor::class,
    TestSecurityConfig::class
)
class RateLimitTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var bookService: BookService

    @Test
    fun `requests within limit succeed`() {
        whenever(bookService.findAll(any()))
            .thenReturn(PageImpl(emptyList()))

        repeat(5) {
            mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk)
        }
    }
}
