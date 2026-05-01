package ch14

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
        expectSuccess = false
    }

    suspend fun getUserReposPage(
        username: String,
        perPage: Int = 30,
        page: Int = 1
    ): Page<Repo> {
        val response = http.get(
            "$baseUrl/users/$username/repos"
        ) {
            url {
                parameters.append(
                    "per_page",
                    perPage.toString()
                )
                parameters.append("page", page.toString())
            }
        }
        val repos = response.body<List<Repo>>()
        val nextUrl = parseLinkNext(
            response.headers["Link"]
        )
        return Page(
            items = repos,
            nextUrl = nextUrl
        )
    }

    suspend fun getAllUserRepos(
        username: String,
        perPage: Int = 100
    ): List<Repo> {
        val all = mutableListOf<Repo>()
        var page = 1
        while (true) {
            val p = getUserReposPage(
                username,
                perPage = perPage,
                page = page
            )
            all.addAll(p.items)
            if (p.nextUrl == null || p.items.isEmpty()) {
                break
            }
            page++
        }
        return all
    }

    fun close() { http.close() }
}
