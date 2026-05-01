package ch47

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation
    .Autowired
import org.springframework.boot.test.autoconfigure
    .web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context
    .SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request
    .MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result
    .MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig::class)
class LibraryIntegrationTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `context loads`() {}

    @Test
    fun `books endpoint accessible`() {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
    }
}
