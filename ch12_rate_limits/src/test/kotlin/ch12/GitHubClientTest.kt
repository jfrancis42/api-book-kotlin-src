package ch12

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GitHubClientTest {
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

    @Test
    fun `getZen returns rate limit info from headers`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("Design for failure.")
                    .addHeader("X-RateLimit-Limit", "60")
                    .addHeader(
                        "X-RateLimit-Remaining", "42"
                    )
                    .addHeader(
                        "X-RateLimit-Reset", "1700000000"
                    )
                    .addHeader("X-RateLimit-Used", "18")
            )
            val result = client.getZen()
            assertTrue(result is ApiResult.Success)
            val resp =
                (result as ApiResult.Success).data
            assertEquals(
                "Design for failure.",
                resp.data
            )
            assertNotNull(resp.rateLimit)
            assertEquals(60, resp.rateLimit!!.limit)
            assertEquals(42, resp.rateLimit!!.remaining)
            assertEquals(18, resp.rateLimit!!.used)
        }

    @Test
    fun `getZen rate limit is null when headers absent`() =
        runTest {
            server.enqueue(
                MockResponse().setBody("ok")
            )
            val result = client.getZen()
            assertTrue(result is ApiResult.Success)
            assertNull(
                (result as ApiResult.Success).data.rateLimit
            )
        }

    @Test
    fun `getZen returns HttpError on 403`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(403)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"message":"rate limit exceeded"}"""
                )
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            403,
            (result as ApiResult.HttpError).status
        )
        assertEquals(
            "rate limit exceeded",
            (result as ApiResult.HttpError).message
        )
    }

    @Test
    fun `getUser includes rate limit info`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""{"login":"alice","id":1}""")
                .addHeader("X-RateLimit-Limit", "5000")
                .addHeader(
                    "X-RateLimit-Remaining", "4999"
                )
                .addHeader(
                    "X-RateLimit-Reset", "1700000000"
                )
                .addHeader("X-RateLimit-Used", "1")
        )
        val result = client.getUser("alice")
        assertTrue(result is ApiResult.Success)
        val resp =
            (result as ApiResult.Success).data
        assertEquals("alice", resp.data.login)
        assertEquals(5000, resp.rateLimit?.limit)
    }

    @Test
    fun `parseRateLimit handles partial headers`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("ok")
                    .addHeader("X-RateLimit-Limit", "60")
                    // missing Remaining and Reset
            )
            val result = client.getZen()
            assertTrue(result is ApiResult.Success)
            // partial headers -> null (all-or-nothing)
            assertNull(
                (result as ApiResult.Success).data.rateLimit
            )
        }
}
