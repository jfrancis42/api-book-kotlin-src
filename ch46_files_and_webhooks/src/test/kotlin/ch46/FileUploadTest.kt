package ch46

import ch46.config.SecurityConfig
import ch46.controller.FileController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(FileController::class)
@Import(SecurityConfig::class, TestSecurityConfig::class)
class FileUploadTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser
    fun `upload valid PDF returns 200`() {
        val file = MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "PDF content".toByteArray()
        )
        mockMvc.perform(
            multipart("/api/files/upload")
                .file(file)
        )
            .andExpect(status().isOk)
            .andExpect(
                jsonPath("$.filename").exists()
            )
    }

    @Test
    @WithMockUser
    fun `upload empty file returns 400`() {
        val file = MockMultipartFile(
            "file", "empty.pdf",
            "application/pdf",
            ByteArray(0)
        )
        mockMvc.perform(
            multipart("/api/files/upload")
                .file(file)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun `upload wrong type returns 400`() {
        val file = MockMultipartFile(
            "file", "script.sh",
            "application/x-sh",
            "#!/bin/bash".toByteArray()
        )
        mockMvc.perform(
            multipart("/api/files/upload")
                .file(file)
        )
            .andExpect(status().isBadRequest)
    }
}
