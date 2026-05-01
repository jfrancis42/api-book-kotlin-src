package ch20

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthTest {
    private lateinit var server: MockWebServer

    @BeforeTest
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @AfterTest
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `token auth sends Bearer header`() = runTest {
        val client = GitHubClient(
            baseUrl = server.url("").toString()
                .trimEnd('/'),
            token = "ghp_test123"
        )
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"alice","id":1}""")
        )

        client.getUser("alice")

        val request = server.takeRequest()
        assertEquals(
            "Bearer ghp_test123",
            request.getHeader("Authorization")
        )
        client.close()
    }

    @Test
    fun `basic auth encodes credentials`() = runTest {
        val client = GitHubClient(
            baseUrl = server.url("").toString()
                .trimEnd('/'),
            basicAuth = "alice" to "password123"
        )
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"alice","id":1}""")
        )

        client.getUser("alice")

        val request = server.takeRequest()
        val authHeader =
            request.getHeader("Authorization") ?: ""
        assertTrue(authHeader.startsWith("Basic "))
        assertFalse(
            authHeader.contains("password123"),
            "Password must not appear in plain text"
        )
        client.close()
    }

    @Test
    fun `api key goes in header not url`() = runTest {
        val client = GitHubClient(
            baseUrl = server.url("").toString()
                .trimEnd('/'),
            apiKey = "secret-key-456"
        )
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"alice","id":1}""")
        )

        client.getUser("alice")

        val request = server.takeRequest()
        assertEquals(
            "secret-key-456",
            request.getHeader("X-Api-Key")
        )
        assertFalse(
            request.path!!.contains("secret-key-456"),
            "API key must not appear in URL"
        )
        client.close()
    }

    @Test
    fun `returns HttpError 401 on Unauthorized`() =
        runTest {
            val client = GitHubClient(
                baseUrl = server.url("").toString()
                    .trimEnd('/')
            )
            server.enqueue(
                MockResponse().setResponseCode(401)
            )

            val result = client.getUser("alice")

            assertTrue(result is ApiResult.HttpError)
            assertEquals(
                401,
                (result as ApiResult.HttpError).status
            )
            client.close()
        }

    @Test
    fun `basicAuthHeader encodes correctly`() {
        val header = basicAuthHeader("user", "pass")
        assertTrue(header.startsWith("Basic "))
        val decoded = String(
            java.util.Base64.getDecoder().decode(
                header.removePrefix("Basic ")
            )
        )
        assertEquals("user:pass", decoded)
    }

    @Test
    fun `maskApiKey hides key value in url`() {
        val url =
            "https://api.example.com?api_key=secret123"
        val masked = maskApiKey(url)
        assertFalse(masked.contains("secret123"))
        assertTrue(masked.contains("api_key=***"))
    }

    @Test
    fun `loadEnvFile parses key value pairs`() {
        val tmpFile = createTempFile("test", ".env")
        tmpFile.writeText(
            "GITHUB_TOKEN=ghp_abc123\n" +
                "# comment\n" +
                "API_KEY=secret\n"
        )
        val env = loadEnvFile(tmpFile.absolutePath)
        assertEquals("ghp_abc123", env["GITHUB_TOKEN"])
        assertEquals("secret", env["API_KEY"])
        tmpFile.delete()
    }
}
