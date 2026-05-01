package ch06

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

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
    fun `getZen returns string`() {
        server.enqueue(
            MockResponse().setBody("Approachable is better.")
        )
        assertEquals(
            "Approachable is better.",
            client.getZen()
        )
    }

    @Test
    fun `getUser parses login and id`() {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":42}"""
                )
        )
        val user = client.getUser("alice")
        assertEquals("alice", user.login)
        assertEquals(42L, user.id)
    }

    @Test
    fun `getUser parses nullable name`() {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":42,""" +
                    """"name":"Alice Smith"}"""
                )
        )
        val user = client.getUser("alice")
        assertEquals("Alice Smith", user.name)
    }

    @Test
    fun `getUser name is null when absent`() {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"bob","id":99}"""
                )
        )
        val user = client.getUser("bob")
        assertNull(user.name)
    }

    @Test
    fun `getUser ignores unknown fields`() {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":42,""" +
                    """"future_field":"ignored"}"""
                )
        )
        val user = client.getUser("alice")
        assertEquals("alice", user.login)
    }

    @Test
    fun `getRepo parses name and full name`() {
        val body = """{
            "id": 2325298,
            "name": "linux",
            "full_name": "torvalds/linux",
            "owner": {"login": "torvalds", "id": 1024025},
            "stargazers_count": 187000
        }""".trimIndent()
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(body)
        )
        val repo = client.getRepo("torvalds", "linux")
        assertEquals("linux", repo.name)
        assertEquals("torvalds/linux", repo.fullName)
        assertEquals(187000, repo.stargazersCount)
    }

    @Test
    fun `getRepo parses nullable description`() {
        val body = """{
            "id": 1,
            "name": "test",
            "full_name": "alice/test",
            "owner": {"login": "alice", "id": 1},
            "description": null
        }""".trimIndent()
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(body)
        )
        val repo = client.getRepo("alice", "test")
        assertNull(repo.description)
    }

    @Test
    fun `getRepo maps snake_case to camelCase`() {
        val body = """{
            "id": 1,
            "name": "test",
            "full_name": "alice/test",
            "owner": {"login": "alice", "id": 1},
            "default_branch": "main",
            "open_issues_count": 5
        }""".trimIndent()
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(body)
        )
        val repo = client.getRepo("alice", "test")
        assertEquals("main", repo.defaultBranch)
        assertEquals(5, repo.openIssuesCount)
    }
}
