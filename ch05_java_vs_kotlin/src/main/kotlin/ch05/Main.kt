package ch05

fun main() {
    println("=== Chapter 5: Java vs Kotlin ===")
    println()

    println("--- Kotlin client ---")
    GitHubClientKotlin().use { client ->
        println("Zen: ${client.getZen()}")
    }

    println()
    println("--- Java client ---")
    GitHubClientJava().use { client ->
        println("Zen: ${client.getZen()}")
    }
}
