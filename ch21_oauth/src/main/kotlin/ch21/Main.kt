package ch21

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val clientId = System.getenv("GITHUB_CLIENT_ID")
        ?: error("Set GITHUB_CLIENT_ID")
    val client = OAuthClient(clientId = clientId)
    try {
        println("=== Chapter 21: OAuth 2.0 ===")
        val result = client.authorizeWithDeviceFlow {
            deviceCode ->
            println(
                "Visit: ${deviceCode.verificationUri}"
            )
            println(
                "Enter code: ${deviceCode.userCode}"
            )
        }
        result.onSuccess { token ->
            println(
                "Got token: ${
                    token.accessToken.take(8)
                }..."
            )
        }
        result.onFailure { e ->
            println("Failed: ${e.message}")
        }
    } finally {
        client.close()
    }
}
