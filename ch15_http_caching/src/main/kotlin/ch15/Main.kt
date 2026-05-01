package ch15

import kotlinx.coroutines.runBlocking
import java.io.File

fun main() = runBlocking {
    val cacheDir = File(
        System.getProperty("java.io.tmpdir"),
        "suspend-disbelief-cache"
    )
    val client = GitHubClient(cacheDir = cacheDir)
    try {
        println("=== Chapter 15: HTTP Caching ===")
        val r = client.getUser("torvalds")
        if (r is ApiResult.Success) {
            println("User: ${r.data.login}")
        }
    } finally {
        client.close()
    }
}
