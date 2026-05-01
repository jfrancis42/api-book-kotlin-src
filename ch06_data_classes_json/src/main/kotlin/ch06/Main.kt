package ch06

fun main() {
    val client = GitHubClient()
    try {
        println("=== Chapter 6: Data Classes and JSON ===")
        println()

        val zen = client.getZen()
        println("GitHub Zen: $zen")
        println()

        val user = client.getUser("torvalds")
        println("User: ${user.name}")
        println("  login:        ${user.login}")
        println("  public repos: ${user.publicRepos}")
        println("  followers:    ${user.followers}")
        println("  member since: ${user.createdAt}")
        println()

        val repo = client.getRepo("torvalds", "linux")
        println("Repo: ${repo.fullName}")
        println("  description: ${repo.description}")
        println("  stars:       ${repo.stargazersCount}")
        println("  forks:       ${repo.forksCount}")
        println("  language:    ${repo.language}")
        println("  branch:      ${repo.defaultBranch}")
    } finally {
        client.close()
    }
}
