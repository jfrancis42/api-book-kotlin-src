package ch16

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConnectionTest {
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
    fun `client makes successful requests`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
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
    fun `multiple requests reuse connection`() = runTest {
        repeat(3) {
            server.enqueue(
                MockResponse()
                    .addHeader(
                        "Content-Type",
                        "application/json"
                    )
                    .setBody("""{"login":"alice","id":1}""")
            )
        }
        repeat(3) { client.getUser("alice") }
        assertEquals(3, server.requestCount)
    }
}
