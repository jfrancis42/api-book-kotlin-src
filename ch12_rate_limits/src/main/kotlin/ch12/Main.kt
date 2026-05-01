package ch12

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GitHubClient()
    try {
        println("=== Chapter 12: Rate Limits ===")
        println()
        when (val r = client.getZen()) {
            is ApiResult.Success -> {
                val rl = r.data.rateLimit
                println("Zen: ${r.data.data}")
                if (rl != null) {
                    println(
                        "Rate limit: ${rl.remaining}" +
                        "/${rl.limit} remaining"
                    )
                }
            }
            is ApiResult.HttpError ->
                println("Error: ${r.status}")
            is ApiResult.NetworkError ->
                println("Network error")
        }
    } finally {
        client.close()
    }
}
