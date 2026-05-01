package ch06

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

private const val DEFAULT_BASE_URL =
    "https://api.github.com"

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
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    fun getZen(): String = runBlocking {
        http.get("$baseUrl/zen").body()
    }

    fun getUser(username: String): User = runBlocking {
        http.get("$baseUrl/users/$username").body()
    }

    fun getRepo(owner: String, repo: String): Repo =
        runBlocking {
            http.get(
                "$baseUrl/repos/$owner/$repo"
            ).body()
        }

    fun close() {
        http.close()
    }
}
