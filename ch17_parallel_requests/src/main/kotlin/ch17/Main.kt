package ch17

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GitHubClient()
    try {
        println("=== Chapter 17: Parallel Requests ===")
        val (user, repo) = client.getUserAndRepos(
            "torvalds",
            "torvalds",
            "linux"
        )
        if (user is ApiResult.Success) {
            println("User: ${user.data.login}")
        }
        if (repo is ApiResult.Success) {
            println("Repo: ${repo.data.fullName}")
        }
    } finally {
        client.close()
    }
}
