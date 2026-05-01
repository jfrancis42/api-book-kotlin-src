package ch20

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DEFAULT_BASE_URL =
    "https://api.github.com"

enum class AuthStrategy { TOKEN, BASIC, API_KEY }

class GitHubClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val token: String? = null,
    private val basicAuth: Pair<String, String>? = null,
    private val apiKey: String? = null
) {
    private val http = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                append(
                    HttpHeaders.Accept,
                    "application/vnd.github.v3+json"
                )
                append(
                    HttpHeaders.UserAgent,
                    "suspend-disbelief-book/1.0"
                )
                when {
                    token != null ->
                        append(
                            HttpHeaders.Authorization,
                            "Bearer $token"
                        )
                    basicAuth != null ->
                        append(
                            HttpHeaders.Authorization,
                            basicAuthHeader(
                                basicAuth.first,
                                basicAuth.second
                            )
                        )
                    apiKey != null ->
                        append(
                            "X-Api-Key",
                            apiKey
                        )
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        expectSuccess = false
    }

    suspend fun getUser(
        username: String
    ): ApiResult<User> = try {
        val response =
            http.get("$baseUrl/users/$username")
        when {
            response.status.isSuccess() ->
                ApiResult.Success(
                    response.body<User>()
                )
            response.status.value == 401 ->
                ApiResult.HttpError(
                    401,
                    "Unauthorized — check your token"
                )
            response.status.value == 403 ->
                ApiResult.HttpError(
                    403,
                    "Forbidden — insufficient scope"
                )
            else -> toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getAuthenticatedUser(): ApiResult<User> =
        try {
            val response = http.get("$baseUrl/user")
            if (response.status.isSuccess()) {
                ApiResult.Success(
                    response.body<User>()
                )
            } else {
                toHttpError(response)
            }
        } catch (e: IOException) {
            ApiResult.NetworkError(e)
        }

    private suspend fun toHttpError(
        response: HttpResponse
    ): ApiResult.HttpError {
        val message = try {
            response.body<GitHubError>().message
        } catch (_: Exception) {
            response.status.description
        }
        return ApiResult.HttpError(
            status = response.status.value,
            message = message
        )
    }

    fun close() { http.close() }
}
