package ch43

import ch43.config.SecurityConfig
import ch43.controller.BookController
import ch43.service.BookService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.security.test.context
    .support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*

@WebMvcTest(BookController::class)
@Import(SecurityConfig::class, TestSecurityConfig::class)
class SecurityTest {
    @Autowired lateinit var mockMvc: MockMvc
    @MockBean lateinit var bookService: BookService

    @Test
    fun `public GET books returns 200`() {
        whenever(bookService.findAll(any()))
            .thenReturn(PageImpl(emptyList()))
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
    }

    @Test
    fun `unauthenticated POST returns 401`() {
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    "application/json"
                )
                .content("{}")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["LIBRARIAN"])
    fun `librarian can POST`() {
        // Validation will fail (empty body)
        // but we get 400 not 401/403
        mockMvc.perform(
            post("/api/books")
                .contentType(
                    "application/json"
                )
                .content("{}")
        )
            .andExpect(status().isBadRequest)
    }
}
