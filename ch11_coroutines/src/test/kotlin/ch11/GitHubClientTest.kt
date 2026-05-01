package ch11

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `getZen is a suspend function returning Success`() =
        runTest {
            server.enqueue(
                MockResponse().setBody("Design for failure.")
            )
            val result = client.getZen()
            assertTrue(result is ApiResult.Success)
            assertEquals(
                "Design for failure.",
                (result as ApiResult.Success).data
            )
        }

    @Test
    fun `getZen returns HttpError on 404`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"message":"Not Found"}""")
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `getUser is a suspend function`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":1}"""
                )
        )
        val result = client.getUser("alice")
        assertTrue(result is ApiResult.Success)
        assertEquals(
            "alice",
            (result as ApiResult.Success).data.login
        )
    }

    @Test
    fun `getUser returns HttpError on 401`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"message":"Requires authentication"}"""
                )
        )
        val result = client.getUser("private")
        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            401,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `multiple suspend calls run sequentially`() = runTest {
        server.enqueue(
            MockResponse().setBody("Zen 1")
        )
        server.enqueue(
            MockResponse().setBody("Zen 2")
        )
        val r1 = client.getZen()
        val r2 = client.getZen()
        assertEquals(
            "Zen 1",
            (r1 as ApiResult.Success).data
        )
        assertEquals(
            "Zen 2",
            (r2 as ApiResult.Success).data
        )
    }
}
