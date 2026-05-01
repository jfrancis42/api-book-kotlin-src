package ch05

import okhttp3.OkHttpClient
import okhttp3.Request

private const val DEFAULT_BASE_URL =
    "https://api.github.com"

class GitHubClientKotlin(
    private val baseUrl: String = DEFAULT_BASE_URL
) : AutoCloseable {

    private val http = OkHttpClient()

    private fun get(path: String): String {
        val request = Request.Builder()
            .url("$baseUrl$path")
            .header(
                "Accept",
                "application/vnd.github.v3+json"
            )
            .header(
                "User-Agent",
                "suspend-disbelief-book/1.0"
            )
            .build()

        return http.newCall(request).execute()
            .use { it.body!!.string() }
    }

    fun getZen(): String = get("/zen")

    fun getUser(username: String): String =
        get("/users/$username")

    fun getRepo(owner: String, repo: String): String =
        get("/repos/$owner/$repo")

    override fun close() {
        http.dispatcher.executorService.shutdown()
        http.connectionPool.evictAll()
    }
}
