package ch13

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

fun ApiResult<*>.isRetryable(): Boolean = when (this) {
    is ApiResult.NetworkError -> true
    is ApiResult.HttpError -> status >= 500
    is ApiResult.Success -> false
}
