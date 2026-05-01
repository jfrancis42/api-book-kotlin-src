package ch20

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val token = System.getenv("GITHUB_TOKEN")
    val client = GitHubClient(token = token)
    try {
        println("=== Chapter 20: Auth ===")
        val result = client.getAuthenticatedUser()
        when (result) {
            is ApiResult.Success ->
                println("Logged in as: " +
                    result.data.login)
            is ApiResult.HttpError ->
                println("Auth failed: " +
                    result.message)
            is ApiResult.NetworkError ->
                println("Network error: " +
                    result.cause.message)
        }
    } finally {
        client.close()
    }
}
