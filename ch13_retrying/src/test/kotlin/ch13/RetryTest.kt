package ch13

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RetryTest {
    private lateinit var server: MockWebServer
    private lateinit var client: GitHubClient

    @BeforeTest
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = GitHubClient(
            baseUrl = server.url("").toString()
                .trimEnd('/')
        )
    }

    @AfterTest
    fun tearDown() {
        client.close()
        server.shutdown()
    }

    private fun userJson(login: String = "alice") =
        MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody("""{"login":"$login","id":1}""")

    @Test
    fun `success on first attempt returns data`() =
        runTest {
            server.enqueue(userJson())
            val result = client.getUserWithRetry(
                "alice",
                maxAttempts = 3,
                initialDelayMs = 1L
            )
            assertTrue(result is ApiResult.Success)
            assertEquals(
                "alice",
                (result as ApiResult.Success).data.login
            )
            assertEquals(1, server.requestCount)
        }

    @Test
    fun `retries on 503 then succeeds`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(503)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"message":"Unavailable"}""")
        )
        server.enqueue(userJson())
        val result = client.getUserWithRetry(
            "alice",
            maxAttempts = 3,
            initialDelayMs = 1L
        )
        assertTrue(result is ApiResult.Success)
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `does not retry on 404`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"message":"Not Found"}""")
        )
        val result = client.getUserWithRetry(
            "nobody",
            maxAttempts = 3,
            initialDelayMs = 1L
        )
        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `exhausts all attempts and returns last error`() =
        runTest {
            repeat(3) {
                server.enqueue(
                    MockResponse()
                        .setResponseCode(500)
                        .addHeader(
                            "Content-Type",
                            "application/json"
                        )
                        .setBody("""{"message":"Error"}""")
                )
            }
            val result = client.getUserWithRetry(
                "alice",
                maxAttempts = 3,
                initialDelayMs = 1L
            )
            assertTrue(result is ApiResult.HttpError)
            assertEquals(
                500,
                (result as ApiResult.HttpError).status
            )
            assertEquals(3, server.requestCount)
        }

    @Test
    fun `isRetryable is true for NetworkError`() {
        val r = ApiResult.NetworkError(
            RuntimeException("timeout")
        )
        assertTrue(r.isRetryable())
    }

    @Test
    fun `isRetryable is true for 5xx HttpError`() {
        assertTrue(
            ApiResult.HttpError(500, "error").isRetryable()
        )
        assertTrue(
            ApiResult.HttpError(503, "error").isRetryable()
        )
    }

    @Test
    fun `isRetryable is false for 4xx HttpError`() {
        assertTrue(
            !ApiResult.HttpError(404, "nf").isRetryable()
        )
        assertTrue(
            !ApiResult.HttpError(401, "auth").isRetryable()
        )
    }
}
