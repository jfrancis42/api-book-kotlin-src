package ch08

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

    private fun jsonResponse(body: String) =
        MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(body)

    @Test
    fun `getUserRepos sends correct path`() {
        server.enqueue(jsonResponse("[]"))
        client.getUserRepos("alice")
        val path = server.takeRequest().path ?: ""
        assertTrue(
            path.startsWith("/users/alice/repos"),
            "Expected /users/alice/repos, got $path"
        )
    }

    @Test
    fun `getUserRepos includes sort param`() {
        server.enqueue(jsonResponse("[]"))
        client.getUserRepos("alice", sort = "updated")
        val path = server.takeRequest().path ?: ""
        assertTrue(
            path.contains("sort=updated"),
            "Expected sort=updated in $path"
        )
    }

    @Test
    fun `getUserRepos includes per_page param`() {
        server.enqueue(jsonResponse("[]"))
        client.getUserRepos("alice", perPage = 10)
        val path = server.takeRequest().path ?: ""
        assertTrue(
            path.contains("per_page=10"),
            "Expected per_page=10 in $path"
        )
    }

    @Test
    fun `getUserRepos with no params has no query string`() {
        server.enqueue(jsonResponse("[]"))
        client.getUserRepos("alice")
        val path = server.takeRequest().path ?: ""
        assertTrue(
            !path.contains("?"),
            "Expected no query string, got $path"
        )
    }

    @Test
    fun `searchRepos sends q parameter`() {
        val body = """{
            "total_count": 0,
            "incomplete_results": false,
            "items": []
        }""".trimIndent()
        server.enqueue(jsonResponse(body))
        client.searchRepos("kotlin coroutines")
        val path = server.takeRequest().path ?: ""
        assertTrue(
            path.contains("q="),
            "Expected q= param in $path"
        )
        assertTrue(
            path.contains("kotlin"),
            "Expected 'kotlin' in query param, got $path"
        )
    }

    @Test
    fun `searchRepos appends language qualifier`() {
        val body = """{
            "total_count": 0,
            "incomplete_results": false,
            "items": []
        }""".trimIndent()
        server.enqueue(jsonResponse(body))
        client.searchRepos(
            "http",
            SearchParams(language = "kotlin")
        )
        val path = server.takeRequest().path ?: ""
        assertTrue(
            path.contains("language%3Akotlin") ||
            path.contains("language:kotlin"),
            "Expected language:kotlin in query, got $path"
        )
    }

    @Test
    fun `searchRepos parses total count`() {
        val body = """{
            "total_count": 42,
            "incomplete_results": false,
            "items": []
        }""".trimIndent()
        server.enqueue(jsonResponse(body))
        val result = client.searchRepos("test")
        assertEquals(42, result.totalCount)
    }

    @Test
    fun `searchRepos parses items`() {
        val body = """{
            "total_count": 1,
            "incomplete_results": false,
            "items": [{
                "id": 1,
                "name": "myrepo",
                "full_name": "alice/myrepo",
                "stargazers_count": 99
            }]
        }""".trimIndent()
        server.enqueue(jsonResponse(body))
        val result = client.searchRepos("test")
        assertEquals(1, result.items.size)
        assertEquals("myrepo", result.items[0].name)
        assertEquals(99, result.items[0].stargazersCount)
    }
}
