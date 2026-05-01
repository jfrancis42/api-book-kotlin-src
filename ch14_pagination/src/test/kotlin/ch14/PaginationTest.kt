package ch14

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class PaginationTest {
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

    private fun repoJson(name: String) =
        """{"id":1,"name":"$name","full_name":"alice/$name"}"""

    private fun reposResponse(
        vararg names: String,
        nextUrl: String? = null
    ): MockResponse {
        val body = "[${names.joinToString(",") { repoJson(it) }}]"
        val r = MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(body)
        if (nextUrl != null) {
            r.addHeader(
                "Link",
                """<$nextUrl>; rel="next""""
            )
        }
        return r
    }

    @Test
    fun `getUserReposPage returns items`() = runTest {
        server.enqueue(
            reposResponse("repo-a", "repo-b")
        )
        val page = client.getUserReposPage("alice")
        assertEquals(2, page.items.size)
        assertEquals("repo-a", page.items[0].name)
        assertEquals("repo-b", page.items[1].name)
    }

    @Test
    fun `getUserReposPage parses next link`() = runTest {
        val nextUrl =
            server.url("/users/alice/repos?page=2")
                .toString()
        server.enqueue(
            reposResponse("repo-a", nextUrl = nextUrl)
        )
        val page = client.getUserReposPage("alice")
        assertNotNull(page.nextUrl)
        assertEquals(nextUrl, page.nextUrl)
    }

    @Test
    fun `getUserReposPage nextUrl is null on last page`() =
        runTest {
            server.enqueue(
                reposResponse("repo-a")
            )
            val page = client.getUserReposPage("alice")
            assertNull(page.nextUrl)
        }

    @Test
    fun `getAllUserRepos collects multiple pages`() =
        runTest {
            val nextUrl =
                server.url("/users/alice/repos?page=2")
                    .toString()
            server.enqueue(
                reposResponse(
                    "repo-a",
                    nextUrl = nextUrl
                )
            )
            server.enqueue(
                reposResponse("repo-b")
            )
            val all = client.getAllUserRepos("alice")
            assertEquals(2, all.size)
            assertEquals("repo-a", all[0].name)
            assertEquals("repo-b", all[1].name)
            assertEquals(2, server.requestCount)
        }

    @Test
    fun `getAllUserRepos stops on empty page`() = runTest {
        server.enqueue(reposResponse())
        val all = client.getAllUserRepos("alice")
        assertEquals(0, all.size)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `parseLinkNext extracts next URL`() {
        val header =
            """<https://api.github.com/repos?page=2>""" +
            """; rel="next", """ +
            """<https://api.github.com/repos?page=5>""" +
            """; rel="last""""
        val next = parseLinkNext(header)
        assertEquals(
            "https://api.github.com/repos?page=2",
            next
        )
    }

    @Test
    fun `parseLinkNext returns null when no next`() {
        val header =
            """<https://api.github.com/repos?page=4>""" +
            """; rel="prev", """ +
            """<https://api.github.com/repos?page=5>""" +
            """; rel="last""""
        assertNull(parseLinkNext(header))
    }

    @Test
    fun `parseLinkNext handles null header`() {
        assertNull(parseLinkNext(null))
    }
}
