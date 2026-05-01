package ch11

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GitHubClient()
    try {
        println("=== Chapter 11: Coroutines ===")
        println()

        when (val result = client.getZen()) {
            is ApiResult.Success ->
                println("Zen: ${result.data}")
            is ApiResult.HttpError ->
                println("Error: ${result.status}")
            is ApiResult.NetworkError ->
                println("Network: ${result.cause}")
        }
        println()

        when (val result = client.getUser("torvalds")) {
            is ApiResult.Success -> {
                val user = result.data
                println("User: ${user.name}")
                println(
                    "  repos: ${user.publicRepos}"
                )
            }
            is ApiResult.HttpError ->
                println(
                    "HTTP Error: ${result.status}"
                )
            is ApiResult.NetworkError ->
                println("Network Error")
        }
    } finally {
        client.close()
    }
}
