package ch09

fun main() {
    val token = System.getenv("GITHUB_TOKEN")
    val owner = System.getenv("GITHUB_OWNER") ?: "alice"
    val repo = System.getenv("GITHUB_REPO") ?: "test-repo"

    if (token == null) {
        println(
            "Set GITHUB_TOKEN, GITHUB_OWNER, " +
            "GITHUB_REPO to run this example"
        )
        return
    }

    val client = GitHubClient(token = token)
    try {
        println("=== Chapter 9: Sending Data ===")
        println()

        val issue = client.createIssue(
            owner,
            repo,
            CreateIssueRequest(
                title = "Test issue from Chapter 9",
                body = "Created by suspend-disbelief-book"
            )
        )
        println(
            "Created issue #${issue.number}: ${issue.title}"
        )
        println("  URL: ${issue.htmlUrl}")
        println()

        val updated = client.updateIssue(
            owner,
            repo,
            issue.number,
            UpdateIssueRequest(
                body = "Updated body from Chapter 9"
            )
        )
        println(
            "Updated issue #${updated.number}: " +
            "${updated.body}"
        )

        val closed = client.closeIssue(
            owner,
            repo,
            issue.number
        )
        println("Closed issue: ${closed.state}")
    } finally {
        client.close()
    }
}
