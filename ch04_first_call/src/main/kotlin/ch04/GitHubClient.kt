package ch04

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

private const val DEFAULT_BASE_URL = "https://api.github.com"

class GitHubClient(
    private val baseUrl: String = DEFAULT_BASE_URL
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
            }
        }
    }

    fun getZen(): String = runBlocking {
        http.get("$baseUrl/zen").bodyAsText()
    }

    fun getUser(username: String): String = runBlocking {
        http.get("$baseUrl/users/$username").bodyAsText()
    }

    fun getRepo(owner: String, repo: String): String = runBlocking {
        http.get("$baseUrl/repos/$owner/$repo").bodyAsText()
    }

    fun close() {
        http.close()
    }
}
