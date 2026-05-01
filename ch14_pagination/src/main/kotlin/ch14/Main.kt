package ch14

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = GitHubClient()
    try {
        println("=== Chapter 14: Pagination ===")
        println()
        val page1 = client.getUserReposPage(
            "torvalds",
            perPage = 5,
            page = 1
        )
        println(
            "Page 1 repos (${page1.items.size}):"
        )
        page1.items.forEach {
            println("  ${it.name}")
        }
        println(
            "Has next page: ${page1.nextUrl != null}"
        )
    } finally {
        client.close()
    }
}
