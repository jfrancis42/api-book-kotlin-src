package ch18

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `getUser sends GET to correct path`() = runTest {
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
        assertEquals("GET", request.method)
        assertEquals("/users/alice", request.path)
    }

    @Test
    fun `getUser sends auth and accept headers`() =
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

            client.getUser("alice")

            val request = server.takeRequest()
            assertEquals(
                "Bearer test-token",
                request.getHeader("Authorization")
            )
            assertEquals(
                "application/json",
                request.getHeader("Accept")
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
        assertEquals(
            "Not Found",
            (result as ApiResult.HttpError).message
        )
    }

    @Test
    fun `getUser returns HttpError on 500`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(500)
        )

        val result = client.getUser("alice")

        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            500,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `getUser returns NetworkError on disconnect`() =
        runTest {
            server.enqueue(
                MockResponse().apply {
                    socketPolicy =
                        SocketPolicy.DISCONNECT_AT_START
                }
            )

            val result = client.getUser("alice")

            assertTrue(result is ApiResult.NetworkError)
        }

    @Test
    fun `createIssue sends POST with correct body`() =
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
                            """"title":"Bug report"}"""
                    )
            )

            client.createIssue(
                owner = "alice",
                repo = "project",
                body = IssueBody(
                    title = "Bug report",
                    body = "Steps to reproduce..."
                )
            )

            val request = server.takeRequest()
            assertEquals("POST", request.method)
            assertEquals(
                "/repos/alice/project/issues",
                request.path
            )
            val bodyText = request.body.readUtf8()
            val json = Json.parseToJsonElement(bodyText)
            val obj = json.jsonObject
            assertEquals(
                "Bug report",
                obj["title"]?.jsonPrimitive?.content
            )
        }

    @Test
    fun `request count tracks all requests`() = runTest {
        repeat(3) {
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
        }

        repeat(3) { client.getUser("alice") }

        assertEquals(3, server.requestCount)
    }
}
