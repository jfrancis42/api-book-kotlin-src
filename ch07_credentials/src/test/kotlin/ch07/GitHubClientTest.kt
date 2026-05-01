package ch07

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GitHubClientTest {
    private lateinit var server: MockWebServer

    @BeforeTest
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterTest
    fun tearDown() {
        server.shutdown()
    }

    private fun baseUrl() =
        server.url("").toString().trimEnd('/')

    @Test
    fun `no token sends no Authorization header`() {
        server.enqueue(MockResponse().setBody("ok"))
        val client = GitHubClient(baseUrl = baseUrl())
        client.getZen()
        client.close()
        val req = server.takeRequest()
        assertNull(
            req.getHeader("Authorization"),
            "Should not send Authorization without token"
        )
    }

    @Test
    fun `token is sent as Bearer header`() {
        server.enqueue(MockResponse().setBody("ok"))
        val client = GitHubClient(
            baseUrl = baseUrl(),
            token = "my-secret-token"
        )
        client.getZen()
        client.close()
        val req = server.takeRequest()
        assertEquals(
            "Bearer my-secret-token",
            req.getHeader("Authorization")
        )
    }

    @Test
    fun `getAuthenticatedUser uses user path`() {
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
        val client = GitHubClient(
            baseUrl = baseUrl(),
            token = "tok"
        )
        client.getAuthenticatedUser()
        client.close()
        assertEquals("/user", server.takeRequest().path)
    }

    @Test
    fun `getUser uses users username path`() {
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
        val client = GitHubClient(baseUrl = baseUrl())
        client.getUser("alice")
        client.close()
        assertEquals(
            "/users/alice",
            server.takeRequest().path
        )
    }

    @Test
    fun `getAuthenticatedUser parses private fields`() {
        val body = """{
            "login": "alice",
            "id": 1,
            "total_private_repos": 5,
            "private_gists": 3
        }""".trimIndent()
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(body)
        )
        val client = GitHubClient(
            baseUrl = baseUrl(),
            token = "tok"
        )
        val user = client.getAuthenticatedUser()
        client.close()
        assertEquals(5, user.totalPrivateRepos)
        assertEquals(3, user.privateGists)
    }
}
