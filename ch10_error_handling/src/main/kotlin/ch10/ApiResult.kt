package ch10

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

inline fun <T, R> ApiResult<T>.map(
    transform: (T) -> R
): ApiResult<R> = when (this) {
    is ApiResult.Success ->
        ApiResult.Success(transform(data))
    is ApiResult.HttpError -> this
    is ApiResult.NetworkError -> this
}

fun <T> ApiResult<T>.getOrNull(): T? = when (this) {
    is ApiResult.Success -> data
    else -> null
}

fun <T> ApiResult<T>.getOrThrow(): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.HttpError ->
        error("HTTP $status: $message")
    is ApiResult.NetworkError ->
        throw cause
}

fun <T> ApiResult<T>.isSuccess() =
    this is ApiResult.Success
