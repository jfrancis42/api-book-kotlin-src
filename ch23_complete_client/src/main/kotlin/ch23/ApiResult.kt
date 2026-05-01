package ch23

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class HttpError(
        val status: Int,
        val message: String
    ) : ApiResult<Nothing>()
    data class NetworkError(
        val cause: Throwable
    ) : ApiResult<Nothing>()

    fun isSuccess() = this is Success<*>

    fun <R> map(
        transform: (T) -> R
    ): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is HttpError -> this
        is NetworkError -> this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is HttpError -> error("HTTP $status: $message")
        is NetworkError -> throw cause
    }
}

fun ApiResult<*>.isRetryable(): Boolean = when (this) {
    is ApiResult.NetworkError -> true
    is ApiResult.HttpError -> status >= 500
    is ApiResult.Success -> false
}
