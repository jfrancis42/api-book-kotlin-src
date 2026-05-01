package ch22

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class HttpError(
        val status: Int,
        val message: String
    ) : ApiResult<Nothing>()
    data class NetworkError(
        val cause: Throwable
    ) : ApiResult<Nothing>()
}

fun <T> ApiResult<T>.getOrThrow(): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.HttpError ->
        error("HTTP $status: $message")
    is ApiResult.NetworkError -> throw cause
}
