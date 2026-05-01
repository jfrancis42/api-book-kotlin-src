package ch04

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
        client = GitHubClient(baseUrl = server.url("").toString()
            .trimEnd('/'))
    }

    @AfterTest
    fun tearDown() {
        client.close()
        server.shutdown()
    }

    @Test
    fun `getZen returns response body`() {
        server.enqueue(
            MockResponse().setBody("Keep it logically awesome.")
        )
        val zen = client.getZen()
        assertEquals("Keep it logically awesome.", zen)
    }

    @Test
    fun `getZen sends correct path`() {
        server.enqueue(MockResponse().setBody("ok"))
        client.getZen()
        val request = server.takeRequest()
        assertEquals("/zen", request.path)
    }

    @Test
    fun `getZen sends Accept header`() {
        server.enqueue(MockResponse().setBody("ok"))
        client.getZen()
        val request = server.takeRequest()
        assertTrue(
            request.getHeader("Accept")
                ?.contains("github") == true,
            "Accept header should reference GitHub API"
        )
    }

    @Test
    fun `getZen sends User-Agent header`() {
        server.enqueue(MockResponse().setBody("ok"))
        client.getZen()
        val request = server.takeRequest()
        assertTrue(
            request.getHeader("User-Agent") != null,
            "User-Agent header should be present"
        )
    }

    @Test
    fun `getUser sends correct path`() {
        server.enqueue(
            MockResponse().setBody("""{"login":"alice"}""")
        )
        client.getUser("alice")
        val request = server.takeRequest()
        assertEquals("/users/alice", request.path)
    }

    @Test
    fun `getUser returns response body`() {
        val body = """{"login":"alice","name":"Alice"}"""
        server.enqueue(MockResponse().setBody(body))
        val result = client.getUser("alice")
        assertEquals(body, result)
    }

    @Test
    fun `getRepo sends correct path`() {
        server.enqueue(
            MockResponse().setBody("""{"name":"linux"}""")
        )
        client.getRepo("torvalds", "linux")
        val request = server.takeRequest()
        assertEquals("/repos/torvalds/linux", request.path)
    }

    @Test
    fun `getRepo returns response body`() {
        val body = """{"name":"linux","owner":"torvalds"}"""
        server.enqueue(MockResponse().setBody(body))
        val result = client.getRepo("torvalds", "linux")
        assertEquals(body, result)
    }
}
