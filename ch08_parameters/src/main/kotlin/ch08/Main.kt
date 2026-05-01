package ch08

fun main() {
    val token = System.getenv("GITHUB_TOKEN")
    val client = GitHubClient(token = token)
    try {
        println("=== Chapter 8: Parameters ===")
        println()

        val repos = client.getUserRepos(
            "torvalds",
            sort = "updated",
            perPage = 5
        )
        println("torvalds' 5 most recently updated repos:")
        repos.forEach { repo ->
            println("  ${repo.name}: ${repo.description}")
        }
        println()

        val result = client.searchRepos(
            "kotlin coroutines",
            SearchParams(
                language = "kotlin",
                sort = "stars",
                order = "desc",
                perPage = 5
            )
        )
        println(
            "Top Kotlin coroutines repos " +
            "(${result.totalCount} total):"
        )
        result.items.forEach { repo ->
            println(
                "  ${repo.fullName}: " +
                "${repo.stargazersCount} stars"
            )
        }
    } finally {
        client.close()
    }
}
