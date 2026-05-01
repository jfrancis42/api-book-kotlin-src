package ch18

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

class GitHubClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val token: String? = null
) {
    private val http = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                append(
                    HttpHeaders.Accept,
                    "application/json"
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
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<User>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getRepo(
        owner: String,
        repo: String
    ): ApiResult<Repo> = try {
        val response =
            http.get("$baseUrl/repos/$owner/$repo")
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<Repo>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun createIssue(
        owner: String,
        repo: String,
        body: IssueBody
    ): ApiResult<Issue> = try {
        val response = http.post(
            "$baseUrl/repos/$owner/$repo/issues"
        ) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<Issue>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getUserRepos(
        username: String
    ): ApiResult<List<Repo>> = try {
        val response =
            http.get("$baseUrl/users/$username/repos")
        if (response.status.isSuccess()) {
            ApiResult.Success(
                response.body<List<Repo>>()
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
