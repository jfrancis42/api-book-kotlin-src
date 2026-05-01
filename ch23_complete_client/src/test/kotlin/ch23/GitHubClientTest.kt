package ch23

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitHubClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: GitHubClient

    @BeforeTest
    fun setup() {
        server = MockWebServer()
        server.start()
        client = GitHubClient(
            baseUrl = server.url("").toString()
                .trimEnd('/'),
            token = "test-token"
        )
    }

    @AfterTest
    fun teardown() {
        client.close()
        server.shutdown()
    }

    @Test
    fun `sends Bearer auth token`() = runTest {
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
            "Bearer test-token",
            request.getHeader("Authorization")
        )
    }

    @Test
    fun `getUser returns Success on 200`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"alice","id":1}""")
        )

        val result = client.getUser("alice")

        assertTrue(result is ApiResult.Success)
        assertEquals(
            "alice",
            (result as ApiResult.Success).data.login
        )
    }

    @Test
    fun `getUser returns HttpError on 404`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"message":"Not Found"}""")
        )

        val result = client.getUser("nobody")

        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `createIssue sends correct POST body`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(201)
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{"id":1,"number":42,""" +
                            """"title":"Bug","state":"open"}"""
                    )
            )

            client.createIssue(
                owner = "alice",
                repo = "project",
                request = CreateIssueRequest(
                    title = "Bug"
                )
            )

            val request = server.takeRequest()
            assertEquals("POST", request.method)
            assertEquals(
                "/repos/alice/project/issues",
                request.path
            )
            val body = request.body.readUtf8()
            assertTrue(body.contains("Bug"))
        }

    @Test
    fun `pagination collects all pages`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .addHeader(
                    "Link",
                    """<${server.url("/page2")}>; rel="next""""
                )
                .setBody(
                    """[{"id":1,"name":"r1",""" +
                        """"full_name":"a/r1"}]"""
                )
        )
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """[{"id":2,"name":"r2",""" +
                        """"full_name":"a/r2"}]"""
                )
        )

        val result = client.getAllUserRepos("alice")

        assertTrue(result is ApiResult.Success)
        assertEquals(
            2,
            (result as ApiResult.Success).data.size
        )
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `rate limit is captured from headers`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .addHeader("X-RateLimit-Limit", "5000")
                    .addHeader(
                        "X-RateLimit-Remaining", "4999"
                    )
                    .addHeader(
                        "X-RateLimit-Reset", "1700000000"
                    )
                    .addHeader("X-RateLimit-Used", "1")
                    .setBody("""{"login":"alice","id":1}""")
            )

            client.getUser("alice")

            val rl = client.currentRateLimit
            assertNotNull(rl)
            assertEquals(5000, rl.limit)
            assertEquals(4999, rl.remaining)
        }

    @Test
    fun `getUserAndRepo makes two parallel requests`() =
        runTest {
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
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody(
                        """{"id":1,"name":"r",""" +
                            """"full_name":"alice/r"}"""
                    )
            )

            val (user, repo) = client.getUserAndRepo(
                "alice", "alice", "r"
            )

            assertTrue(user is ApiResult.Success)
            assertTrue(repo is ApiResult.Success)
            assertEquals(2, server.requestCount)
        }

    @Test
    fun `isRetryable on 500`() {
        val result: ApiResult<User> =
            ApiResult.HttpError(500, "Internal Error")
        assertTrue(result.isRetryable())
    }

    @Test
    fun `isRetryable false on 404`() {
        val result: ApiResult<User> =
            ApiResult.HttpError(404, "Not Found")
        assertTrue(!result.isRetryable())
    }
}
