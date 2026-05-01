package ch13

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GitHubClient()
    try {
        println("=== Chapter 13: Retrying ===")
        val result = client.getUserWithRetry("torvalds")
        when (result) {
            is ApiResult.Success ->
                println("User: ${result.data.login}")
            is ApiResult.HttpError ->
                println("HTTP error: ${result.status}")
            is ApiResult.NetworkError ->
                println("Network error: ${result.cause}")
        }
    } finally {
        client.close()
    }
}
