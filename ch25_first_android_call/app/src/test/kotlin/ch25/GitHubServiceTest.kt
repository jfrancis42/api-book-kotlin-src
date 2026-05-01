package ch25

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var service: GitHubService

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val retrofit = buildRetrofit(
            baseUrl = server.url("/").toString()
        )
        service = retrofit.create(
            GitHubService::class.java
        )
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getUser parses login and id`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":1,""" +
                        """"public_repos":5}"""
                )
        )

        val user = service.getUser("alice")

        assertEquals("alice", user.login)
        assertEquals(1L, user.id)
        assertEquals(5, user.publicRepos)
    }

    @Test
    fun `getUser sends GET to correct path`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"bob","id":2}""")
        )

        service.getUser("bob")

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/users/bob", request.path)
    }

    @Test
    fun `getUser ignores unknown json fields`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{
                      "login":"alice",
                      "id":1,
                      "unknown_field":"value",
                      "another_unknown":42
                    }"""
                )
        )

        val user = service.getUser("alice")

        assertNotNull(user)
        assertEquals("alice", user.login)
    }
}
