package ch17

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParallelTest {
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
    fun `getUserAndRepos makes two requests`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""{"login":"alice","id":1}""")
        )
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """{"id":1,"name":"r","full_name":"alice/r"}"""
                )
        )
        val (user, repo) = client.getUserAndRepos(
            "alice",
            "alice",
            "r"
        )
        assertTrue(user is ApiResult.Success)
        assertTrue(repo is ApiResult.Success)
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `getMultipleUsers fetches all users`() = runTest {
        repeat(3) { i ->
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody("""{"login":"u$i","id":$i}""")
            )
        }
        val results = client.getMultipleUsers(
            listOf("u0", "u1", "u2")
        )
        assertEquals(3, results.size)
        results.forEach { r ->
            assertTrue(r is ApiResult.Success)
        }
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `one failure does not prevent others`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""{"login":"good","id":1}""")
        )
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"message":"Not Found"}""")
        )
        val results = client.getMultipleUsers(
            listOf("good", "missing")
        )
        assertEquals(2, results.size)
        assertTrue(results[0] is ApiResult.Success)
        assertTrue(results[1] is ApiResult.HttpError)
    }
}
