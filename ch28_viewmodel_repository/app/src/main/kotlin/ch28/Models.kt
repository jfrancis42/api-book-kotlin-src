package ch28

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0
)

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

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message: String
    ) : UiState<Nothing>()
}
