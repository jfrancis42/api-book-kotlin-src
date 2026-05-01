package ch10

fun main() {
    val client = GitHubClient()
    try {
        println(
            "=== Chapter 10: Error Handling ==="
        )
        println()

        when (val result = client.getZen()) {
            is ApiResult.Success ->
                println("Zen: ${result.data}")
            is ApiResult.HttpError ->
                println("HTTP Error ${result.status}")
            is ApiResult.NetworkError ->
                println("Network Error: ${result.cause}")
        }
        println()

        // Deliberately trigger a 404
        when (val result =
                client.getUser("zzz-no-such-user-xyz")) {
            is ApiResult.Success ->
                println("User: ${result.data.login}")
            is ApiResult.HttpError ->
                println(
                    "Not found (${result.status}): " +
                    result.message
                )
            is ApiResult.NetworkError ->
                println(
                    "Network error: ${result.cause.message}"
                )
        }
        println()

        // Using map to transform success values
        val nameResult = client.getUser("torvalds")
            .map { it.name ?: it.login }

        when (nameResult) {
            is ApiResult.Success ->
                println("Name: ${nameResult.data}")
            is ApiResult.HttpError ->
                println("Error: ${nameResult.message}")
            is ApiResult.NetworkError ->
                println("Network error")
        }
    } finally {
        client.close()
    }
}
