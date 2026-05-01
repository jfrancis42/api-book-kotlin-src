package ch05

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitHubClientJavaTest {
    private lateinit var server: MockWebServer
    private lateinit var client: GitHubClientJava

    @BeforeTest
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = GitHubClientJava(
            server.url("").toString().trimEnd('/')
        )
    }

    @AfterTest
    fun tearDown() {
        client.close()
        server.shutdown()
    }

    @Test
    fun `getZen returns response body`() {
        server.enqueue(
            MockResponse().setBody("Approachable is better.")
        )
        assertEquals(
            "Approachable is better.",
            client.getZen()
        )
    }

    @Test
    fun `getZen sends correct path`() {
        server.enqueue(MockResponse().setBody("ok"))
        client.getZen()
        assertEquals("/zen", server.takeRequest().path)
    }

    @Test
    fun `getUser sends correct path`() {
        server.enqueue(
            MockResponse().setBody("""{"login":"bob"}""")
        )
        client.getUser("bob")
        assertEquals(
            "/users/bob",
            server.takeRequest().path
        )
    }

    @Test
    fun `getRepo sends correct path`() {
        server.enqueue(
            MockResponse().setBody("""{"name":"repo"}""")
        )
        client.getRepo("alice", "repo")
        assertEquals(
            "/repos/alice/repo",
            server.takeRequest().path
        )
    }

    @Test
    fun `getZen sends Accept header`() {
        server.enqueue(MockResponse().setBody("ok"))
        client.getZen()
        val req = server.takeRequest()
        assertTrue(
            req.getHeader("Accept")
                ?.contains("github") == true
        )
    }
}
