package ch19

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = AtomClient()
    try {
        println("=== Chapter 19: XML ===")
        val result = client.getReleases(
            "torvalds", "linux"
        )
        result.onSuccess { feed ->
            println("Feed: ${feed.title}")
            println(
                "Entries: ${feed.entries.size}"
            )
            feed.entries.take(3).forEach { entry ->
                println("  - ${entry.title}")
            }
        }
        result.onFailure { e ->
            println("Error: ${e.message}")
        }
    } finally {
        client.close()
    }
}
