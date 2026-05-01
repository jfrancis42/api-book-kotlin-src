package ch41.exception

import ch41.dto.ApiError
import ch41.dto.FieldError
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(
        LibraryException.BookNotFoundException::class
    )
    fun handleNotFound(
        ex: LibraryException.BookNotFoundException
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(404).body(
            ApiError(404, "NOT_FOUND", ex.message!!)
        )

    @ExceptionHandler(
        LibraryException.DuplicateIsbnException::class
    )
    fun handleDuplicate(
        ex: LibraryException.DuplicateIsbnException
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(409).body(
            ApiError(
                409, "DUPLICATE_ISBN",
                ex.message!!
            )
        )

    @ExceptionHandler(
        LibraryException.BookNotAvailableException::class
    )
    fun handleNotAvailable(
        ex: LibraryException.BookNotAvailableException
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(409).body(
            ApiError(
                409, "BOOK_NOT_AVAILABLE",
                ex.message!!
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
