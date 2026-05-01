package ch46.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path

@RestController
@RequestMapping("/api/files")
class FileController {

    private val uploadDir: Path =
        Files.createTempDirectory("library-uploads")

    @PostMapping("/upload")
    fun upload(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest()
                .body(mapOf(
                    "error" to "File is empty"
                ))
        }
        val allowed = setOf(
            "image/jpeg",
            "image/png",
            "application/pdf"
        )
        val contentType =
            file.contentType ?: "unknown"
        if (contentType !in allowed) {
            return ResponseEntity
                .badRequest()
                .body(mapOf(
                    "error" to
                    "Unsupported type: $contentType"
                ))
        }
        val filename =
            "${System.currentTimeMillis()}" +
            "_${file.originalFilename}"
        Files.copy(
            file.inputStream,
            uploadDir.resolve(filename)
        )
        return ResponseEntity.ok(
            mapOf("filename" to filename)
        )
    }
}
