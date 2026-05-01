package ch10

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
    fun `getZen returns Success on 200`() {
        server.enqueue(
            MockResponse().setBody("Keep it simple.")
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.Success)
        assertEquals(
            "Keep it simple.",
            (result as ApiResult.Success).data
        )
    }

    @Test
    fun `getZen returns HttpError on 404`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"message":"Not Found"}"""
                )
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `getZen returns HttpError on 401`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"message":"Unauthorized"}"""
                )
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.HttpError)
        assertEquals(401, (result as ApiResult.HttpError).status)
        assertEquals(
            "Unauthorized",
            (result as ApiResult.HttpError).message
        )
    }

    @Test
    fun `getZen returns HttpError on 429`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(429)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"message":"rate limit exceeded"}"""
                )
        )
        val result = client.getZen()
        assertTrue(result is ApiResult.HttpError)
        assertEquals(429, (result as ApiResult.HttpError).status)
    }

    @Test
    fun `getUser returns Success with parsed user`() {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
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
    fun `getUser returns HttpError on 404`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"message":"Not Found"}"""
                )
        )
        val result = client.getUser("no-such-user")
        assertTrue(result is ApiResult.HttpError)
        assertEquals(404, (result as ApiResult.HttpError).status)
        assertEquals(
            "Not Found",
            (result as ApiResult.HttpError).message
        )
    }

    @Test
    fun `map on Success transforms data`() {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"login":"alice","id":1,""" +
                    """"name":"Alice Smith"}"""
                )
        )
        val result = client.getUser("alice")
            .map { it.name ?: it.login }
        assertTrue(result is ApiResult.Success)
        assertEquals(
            "Alice Smith",
            (result as ApiResult.Success).data
        )
    }
}
