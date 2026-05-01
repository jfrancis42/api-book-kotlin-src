package ch42.dto

data class FieldError(
    val field: String,
    val message: String
)

data class ApiError(
    val status: Int,
    val code: String,
    val message: String,
    val details: List<FieldError>? = null
)
