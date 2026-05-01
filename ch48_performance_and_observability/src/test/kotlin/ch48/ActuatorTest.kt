package ch48

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
class ActuatorTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `health endpoint returns UP`() {
        mockMvc.perform(
            get("/actuator/health")
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.status")
                    .value("UP")
            )
    }

    @Test
    fun `metrics endpoint is accessible`() {
        mockMvc.perform(
            get("/actuator/metrics")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `prometheus endpoint is accessible`() {
        mockMvc.perform(
            get("/actuator/prometheus")
        )
            .andExpect(status().isOk)
    }
}
