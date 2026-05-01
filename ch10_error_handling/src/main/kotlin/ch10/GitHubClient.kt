package ch10

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DEFAULT_BASE_URL =
    "https://api.github.com"

class GitHubClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val token: String? = null
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

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
                token?.let {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer $it"
                    )
                }
            }
        }
        install(ContentNegotiation) {
            json(json)
        }
        // Disable default exception throwing on 4xx/5xx
        expectSuccess = false
    }

    fun getZen(): ApiResult<String> = runBlocking {
        try {
            val response = http.get("$baseUrl/zen")
            if (response.status.isSuccess()) {
                ApiResult.Success(response.bodyAsText())
            } else {
                toHttpError(response)
            }
        } catch (e: IOException) {
            ApiResult.NetworkError(e)
        }
    }

    fun getUser(
        username: String
    ): ApiResult<User> = runBlocking {
        try {
            val response =
                http.get("$baseUrl/users/$username")
            if (response.status.isSuccess()) {
                ApiResult.Success(response.body<User>())
            } else {
                toHttpError(response)
            }
        } catch (e: IOException) {
            ApiResult.NetworkError(e)
        }
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

    fun close() {
        http.close()
    }
}
