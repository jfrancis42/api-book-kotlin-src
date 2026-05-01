package ch08

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

    fun getUser(username: String): User = runBlocking {
        http.get("$baseUrl/users/$username").body()
    }

    fun getRepo(owner: String, repo: String): Repo =
        runBlocking {
            http.get(
                "$baseUrl/repos/$owner/$repo"
            ).body()
        }

    fun getUserRepos(
        username: String,
        sort: String? = null,
        direction: String? = null,
        perPage: Int? = null,
        page: Int? = null
    ): List<Repo> = runBlocking {
        http.get("$baseUrl/users/$username/repos") {
            url {
                sort?.let { parameters.append("sort", it) }
                direction?.let {
                    parameters.append("direction", it)
                }
                perPage?.let {
                    parameters.append("per_page", it.toString())
                }
                page?.let {
                    parameters.append("page", it.toString())
                }
            }
        }.body()
    }

    fun searchRepos(
        query: String,
        params: SearchParams = SearchParams()
    ): SearchResult = runBlocking {
        http.get("$baseUrl/search/repositories") {
            url {
                val q = buildString {
                    append(query)
                    params.language?.let {
                        append(" language:$it")
                    }
                }
                parameters.append("q", q)
                params.sort?.let {
                    parameters.append("sort", it)
                }
                params.order?.let {
                    parameters.append("order", it)
                }
                params.perPage?.let {
                    parameters.append(
                        "per_page",
                        it.toString()
                    )
                }
                params.page?.let {
                    parameters.append(
                        "page",
                        it.toString()
                    )
                }
            }
        }.body()
    }

    fun close() {
        http.close()
    }
}
