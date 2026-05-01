package ch07

fun main() {
    val token = System.getenv("GITHUB_TOKEN")

    val client = GitHubClient(token = token)
    try {
        println("=== Chapter 7: Keeping Secrets ===")
        println()

        if (token != null) {
            val me = client.getAuthenticatedUser()
            println("Authenticated as: ${me.login}")
            println("Name:             ${me.name}")
            println(
                "Private repos: " +
                "${me.totalPrivateRepos ?: "unknown"}"
            )
            println()
        } else {
            println(
                "No GITHUB_TOKEN set — " +
                "running unauthenticated"
            )
            println()
        }

        val user = client.getUser("torvalds")
        println("Public user: ${user.login}")
        println("  repos: ${user.publicRepos}")
    } finally {
        client.close()
    }
}
