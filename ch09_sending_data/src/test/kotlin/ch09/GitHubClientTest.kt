package ch09

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
                .trimEnd('/'),
            token = "test-token"
        )
    }

    @AfterTest
    fun tearDown() {
        client.close()
        server.shutdown()
    }

    private fun issueJson(
        id: Long = 1L,
        number: Int = 1,
        title: String = "Test",
        state: String = "open"
    ) = """{
        "id": $id,
        "number": $number,
        "title": "$title",
        "state": "$state",
        "html_url": "https://github.com/test"
    }""".trimIndent()

    private fun jsonResponse(body: String) =
        MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(body)

    @Test
    fun `createIssue sends POST to correct path`() {
        server.enqueue(jsonResponse(issueJson()))
        client.createIssue(
            "alice",
            "myrepo",
            CreateIssueRequest(title = "Bug report")
        )
        val req = server.takeRequest()
        assertEquals("POST", req.method)
        assertEquals(
            "/repos/alice/myrepo/issues",
            req.path
        )
    }

    @Test
    fun `createIssue sends JSON body`() {
        server.enqueue(jsonResponse(issueJson()))
        client.createIssue(
            "alice",
            "myrepo",
            CreateIssueRequest(
                title = "Bug",
                body = "Details here"
            )
        )
        val req = server.takeRequest()
        val body = req.body.readUtf8()
        assertTrue(
            body.contains("Bug"),
            "Request body should contain title: $body"
        )
        assertTrue(
            body.contains("Details here"),
            "Request body should contain body: $body"
        )
    }

    @Test
    fun `createIssue returns parsed issue`() {
        server.enqueue(
            jsonResponse(
                issueJson(id = 42, number = 7, title = "Bug")
            )
        )
        val issue = client.createIssue(
            "alice",
            "myrepo",
            CreateIssueRequest(title = "Bug")
        )
        assertEquals(42L, issue.id)
        assertEquals(7, issue.number)
        assertEquals("Bug", issue.title)
    }

    @Test
    fun `updateIssue sends PATCH to correct path`() {
        server.enqueue(jsonResponse(issueJson(number = 5)))
        client.updateIssue(
            "alice",
            "myrepo",
            5,
            UpdateIssueRequest(title = "New title")
        )
        val req = server.takeRequest()
        assertEquals("PATCH", req.method)
        assertEquals(
            "/repos/alice/myrepo/issues/5",
            req.path
        )
    }

    @Test
    fun `closeIssue sends closed state`() {
        server.enqueue(
            jsonResponse(issueJson(state = "closed"))
        )
        client.closeIssue("alice", "myrepo", 3)
        val req = server.takeRequest()
        assertEquals("PATCH", req.method)
        val body = req.body.readUtf8()
        assertTrue(
            body.contains("closed"),
            "Body should contain closed: $body"
        )
    }

    @Test
    fun `deleteLabel sends DELETE to correct path`() {
        server.enqueue(
            MockResponse().setResponseCode(204)
        )
        val deleted = client.deleteLabel(
            "alice",
            "myrepo",
            "bug"
        )
        val req = server.takeRequest()
        assertEquals("DELETE", req.method)
        assertEquals(
            "/repos/alice/myrepo/labels/bug",
            req.path
        )
        assertTrue(deleted)
    }

    @Test
    fun `createIssue sets Content-Type header`() {
        server.enqueue(jsonResponse(issueJson()))
        client.createIssue(
            "alice",
            "myrepo",
            CreateIssueRequest(title = "Bug")
        )
        val req = server.takeRequest()
        assertTrue(
            req.getHeader("Content-Type")
                ?.contains("application/json") == true,
            "Should set Content-Type: application/json"
        )
    }
}
