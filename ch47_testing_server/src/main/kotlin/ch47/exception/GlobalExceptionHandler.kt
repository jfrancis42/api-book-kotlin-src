package ch47.exception

import ch47.dto.ApiError
import ch47.dto.FieldError
import org.springframework.http.ResponseEntity
import org.springframework.web.bind
    .MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        LibraryException.BookNotFoundException
            ::class
    )
    fun handleNotFound(
        ex: LibraryException.BookNotFoundException
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(404).body(
            ApiError(
                404, "NOT_FOUND", ex.message!!
            )
        )

    @ExceptionHandler(
        MethodArgumentNotValidException::class
    )
    fun handleValidation(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ApiError> {
        val details = ex.bindingResult
            .fieldErrors.map {
                FieldError(
                    it.field,
                    it.defaultMessage ?: "invalid"
                )
            }
        return ResponseEntity.badRequest().body(
            ApiError(
                400, "VALIDATION_ERROR",
                "Validation failed", details
            )
        )
    }
}
