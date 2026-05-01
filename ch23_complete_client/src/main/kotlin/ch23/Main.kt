package ch23

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val token = System.getenv("GITHUB_TOKEN")
    GitHubClient.authenticated(
        token ?: error("Set GITHUB_TOKEN")
    ).use { client ->
        println("=== Chapter 23: Complete Client ===")

        val zen = client.getZen()
        if (zen is ApiResult.Success) {
            println("Zen: ${zen.data}")
        }

        val me = client.getAuthenticatedUser()
        if (me is ApiResult.Success) {
            println("Logged in as: ${me.data.login}")
            println("Public repos: " +
                me.data.publicRepos)
        }

        val (user, repo) = client.getUserAndRepo(
            "torvalds", "torvalds", "linux"
        )
        if (user is ApiResult.Success) {
            println("User: ${user.data.login}")
        }
        if (repo is ApiResult.Success) {
            println(
                "Repo: ${repo.data.fullName} " +
                    "(${repo.data.stargazersCount} stars)"
            )
        }

        client.currentRateLimit?.let { rl ->
            println(
                "Rate limit: ${rl.remaining}/" +
                    "${rl.limit} remaining"
            )
        }
    }
}
