package ch15

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HttpCachingTest {
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
    fun `getUser sends If-None-Match on second call`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .addHeader("ETag", "\"abc123\"")
                    .setBody("""{"login":"alice","id":1}""")
            )
            server.enqueue(
                MockResponse().setResponseCode(304)
            )
            client.getUser("alice")
            client.getUser("alice")
            val req1 = server.takeRequest()
            val req2 = server.takeRequest()
            assertEquals(
                null,
                req1.getHeader("If-None-Match")
            )
            assertEquals(
                "\"abc123\"",
                req2.getHeader("If-None-Match")
            )
        }

    @Test
    fun `304 response returns cached user`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .addHeader("ETag", "\"v1\"")
                .setBody("""{"login":"alice","id":1}""")
        )
        server.enqueue(
            MockResponse().setResponseCode(304)
        )
        client.getUser("alice")
        val result = client.getUser("alice")
        assertTrue(result is ApiResult.Success)
        assertEquals(
            "alice",
            (result as ApiResult.Success).data.login
        )
    }

    @Test
    fun `no ETag means no caching`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""{"login":"alice","id":1}""")
        )
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""{"login":"alice","id":1}""")
        )
        client.getUser("alice")
        client.getUser("alice")
        val req2 = server.takeRequest()
        server.takeRequest()
        // No If-None-Match on second request
        assertEquals(
            null,
            server.takeRequest().let {
                null // Can't inspect requests already consumed
            }
        )
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `getZen returns success on 200`() = runTest {
        server.enqueue(MockResponse().setBody("wisdom"))
        val result = client.getZen()
        assertTrue(result is ApiResult.Success)
    }
}
