package ch04

fun main() {
    val client = GitHubClient()
    try {
        println("=== Chapter 4: Your First API Call ===")
        println()

        val zen = client.getZen()
        println("GitHub Zen: $zen")
        println()

        val user = client.getUser("torvalds")
        println("User (raw JSON):")
        println(user)
        println()

        val repo = client.getRepo("torvalds", "linux")
        println("Repo (raw JSON, first 200 chars):")
        println(repo.take(200) + "...")
    } finally {
        client.close()
    }
}
