package ch09

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
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    fun createIssue(
        owner: String,
        repo: String,
        request: CreateIssueRequest
    ): Issue = runBlocking {
        http.post(
            "$baseUrl/repos/$owner/$repo/issues"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun updateIssue(
        owner: String,
        repo: String,
        issueNumber: Int,
        request: UpdateIssueRequest
    ): Issue = runBlocking {
        http.patch(
            "$baseUrl/repos/$owner/$repo" +
            "/issues/$issueNumber"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun closeIssue(
        owner: String,
        repo: String,
        issueNumber: Int
    ): Issue = runBlocking {
        http.patch(
            "$baseUrl/repos/$owner/$repo" +
            "/issues/$issueNumber"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateIssueRequest(state = "closed")
            )
        }.body()
    }

    fun createLabel(
        owner: String,
        repo: String,
        request: CreateLabelRequest
    ): Label = runBlocking {
        http.post(
            "$baseUrl/repos/$owner/$repo/labels"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun deleteLabel(
        owner: String,
        repo: String,
        name: String
    ): Boolean = runBlocking {
        val response = http.delete(
            "$baseUrl/repos/$owner/$repo/labels/$name"
        )
        response.status == HttpStatusCode.NoContent
    }

    fun starRepo(owner: String, repo: String) =
        runBlocking {
            http.put(
                "$baseUrl/user/starred/$owner/$repo"
            ) {
                contentType(ContentType.Application.Json)
                setBody("")
            }
        }

    fun unstarRepo(owner: String, repo: String) =
        runBlocking {
            http.delete(
                "$baseUrl/user/starred/$owner/$repo"
            )
        }

    fun close() {
        http.close()
    }
}
