package ch23

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.ConnectionPool
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val DEFAULT_BASE_URL =
    "https://api.github.com"
private val LINK_NEXT_RE = Regex(
    """<([^>]+)>\s*;\s*rel="next""""
)

class GitHubClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val token: String? = null,
    private val cacheDir: File? = null,
    private val cacheSizeBytes: Long =
        10L * 1024 * 1024
) : Closeable {

    companion object {
        fun authenticated(token: String) =
            GitHubClient(token = token)

        fun unauthenticated() = GitHubClient()

        fun withCache(
            token: String,
            cacheDir: File
        ) = GitHubClient(
            token = token,
            cacheDir = cacheDir
        )
    }

    @Volatile
    private var lastRateLimit: RateLimitInfo? = null

    val currentRateLimit: RateLimitInfo?
        get() = lastRateLimit

    private val http = HttpClient(OkHttp) {
        engine {
            config {
                connectionPool(
                    ConnectionPool(
                        maxIdleConnections = 5,
                        keepAliveDuration = 5,
                        timeUnit = TimeUnit.MINUTES
                    )
                )
                connectTimeout(10, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
                cacheDir?.let {
                    cache(Cache(it, cacheSizeBytes))
                }
            }
        }
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

    suspend fun getZen(): ApiResult<String> = try {
        val response =
            http.get("$baseUrl/zen")
        if (response.status.isSuccess()) {
            ApiResult.Success(response.bodyAsText())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getUser(
        username: String
    ): ApiResult<User> = try {
        val response =
            http.get("$baseUrl/users/$username")
        captureRateLimit(response)
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<User>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getAuthenticatedUser(): ApiResult<User> =
        try {
            val response = http.get("$baseUrl/user")
            captureRateLimit(response)
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

    suspend fun getRepo(
        owner: String,
        repo: String
    ): ApiResult<Repo> = try {
        val response =
            http.get("$baseUrl/repos/$owner/$repo")
        captureRateLimit(response)
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<Repo>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun searchRepos(
        query: String,
        language: String? = null,
        sort: String? = null,
        perPage: Int = 30
    ): ApiResult<SearchResult> = try {
        val q = buildString {
            append(query)
            language?.let { append(" language:$it") }
        }
        val response = http.get("$baseUrl/search/repositories") {
            url {
                parameters.append("q", q)
                sort?.let {
                    parameters.append("sort", it)
                }
                parameters.append(
                    "per_page", perPage.toString()
                )
            }
        }
        captureRateLimit(response)
        if (response.status.isSuccess()) {
            ApiResult.Success(
                response.body<SearchResult>()
            )
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getUserReposPage(
        username: String,
        perPage: Int = 30,
        url: String? = null
    ): ApiResult<Page<Repo>> = try {
        val requestUrl = url
            ?: "$baseUrl/users/$username/repos"
        val response = http.get(requestUrl) {
            if (url == null) {
                this.url {
                    parameters.append(
                        "per_page",
                        perPage.toString()
                    )
                }
            }
        }
        captureRateLimit(response)
        if (response.status.isSuccess()) {
            val items = response.body<List<Repo>>()
            val nextUrl = response
                .headers["Link"]
                ?.let { parseLinkNext(it) }
            ApiResult.Success(
                Page(items = items, nextUrl = nextUrl)
            )
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getAllUserRepos(
        username: String
    ): ApiResult<List<Repo>> {
        val all = mutableListOf<Repo>()
        var nextUrl: String? = null
        var first = true
        while (first || nextUrl != null) {
            first = false
            val result = getUserReposPage(
                username = username,
                url = nextUrl
            )
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.items.isEmpty()) {
                        break
                    }
                    all.addAll(result.data.items)
                    nextUrl = result.data.nextUrl
                }
                else -> return result.map { emptyList() }
            }
        }
        return ApiResult.Success(all)
    }

    suspend fun createIssue(
        owner: String,
        repo: String,
        request: CreateIssueRequest
    ): ApiResult<Issue> = try {
        val response = http.post(
            "$baseUrl/repos/$owner/$repo/issues"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        captureRateLimit(response)
        if (response.status.isSuccess()) {
            ApiResult.Success(response.body<Issue>())
        } else {
            toHttpError(response)
        }
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getUserAndRepo(
        username: String,
        owner: String,
        repo: String
    ): Pair<ApiResult<User>, ApiResult<Repo>> =
        coroutineScope {
            val user = async { getUser(username) }
            val repository = async {
                getRepo(owner, repo)
            }
            Pair(user.await(), repository.await())
        }

    private fun captureRateLimit(
        response: HttpResponse
    ) {
        val limit = response.headers[
            "X-RateLimit-Limit"
        ]?.toIntOrNull() ?: return
        val remaining = response.headers[
            "X-RateLimit-Remaining"
        ]?.toIntOrNull() ?: return
        val reset = response.headers[
            "X-RateLimit-Reset"
        ]?.toLongOrNull() ?: return
        val used = response.headers[
            "X-RateLimit-Used"
        ]?.toIntOrNull() ?: 0
        lastRateLimit = RateLimitInfo(
            limit = limit,
            remaining = remaining,
            reset = reset,
            used = used
        )
    }

    private fun parseLinkNext(
        header: String
    ): String? {
        return LINK_NEXT_RE.find(header)
            ?.groupValues?.get(1)
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

    override fun close() { http.close() }
}
