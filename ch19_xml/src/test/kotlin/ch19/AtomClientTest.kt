package ch19

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AtomClientTest {
    private lateinit var server: MockWebServer
    private lateinit var client: AtomClient

    private val sampleAtom = """
        <?xml version="1.0" encoding="UTF-8"?>
        <feed xmlns="http://www.w3.org/2005/Atom">
          <id>tag:github.com,2008:/torvalds/linux</id>
          <title>Releases from linux</title>
          <updated>2026-01-01T00:00:00Z</updated>
          <entry>
            <id>tag:github.com,2008:v6.8</id>
            <title>v6.8</title>
            <updated>2026-01-01T00:00:00Z</updated>
            <link href="https://github.com/t/l/v6.8"
                  rel="alternate"/>
            <content>Release notes v6.8</content>
          </entry>
          <entry>
            <id>tag:github.com,2008:v6.7</id>
            <title>v6.7</title>
            <updated>2025-12-01T00:00:00Z</updated>
            <link href="https://github.com/t/l/v6.7"
                  rel="alternate"/>
            <content>Release notes v6.7</content>
          </entry>
        </feed>
    """.trimIndent()

    @BeforeTest
    fun setup() {
        server = MockWebServer()
        server.start()
        client = AtomClient(
            baseUrl = server.url("").toString()
                .trimEnd('/')
        )
    }

    @AfterTest
    fun teardown() {
        client.close()
        server.shutdown()
    }

    @Test
    fun `parses feed title and id`() {
        val feed = parseAtomFeed(sampleAtom)
        assertEquals(
            "Releases from linux",
            feed.title
        )
        assertTrue(feed.id.isNotEmpty())
    }

    @Test
    fun `parses all entries`() {
        val feed = parseAtomFeed(sampleAtom)
        assertEquals(2, feed.entries.size)
        assertEquals("v6.8", feed.entries[0].title)
        assertEquals("v6.7", feed.entries[1].title)
    }

    @Test
    fun `parses entry link href`() {
        val feed = parseAtomFeed(sampleAtom)
        assertTrue(
            feed.entries[0].link
                .contains("github.com")
        )
    }

    @Test
    fun `parses entry content`() {
        val feed = parseAtomFeed(sampleAtom)
        assertEquals(
            "Release notes v6.8",
            feed.entries[0].content
        )
    }

    @Test
    fun `getReleases makes GET request`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/atom+xml"
                )
                .setBody(sampleAtom)
        )

        val result = client.getReleases(
            "torvalds", "linux"
        )

        assertTrue(result.isSuccess)
        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(
            request.path!!.contains("releases.atom")
        )
    }

    @Test
    fun `getReleases returns failure on HTTP error`() =
        runTest {
            server.enqueue(
                MockResponse().setResponseCode(404)
            )

            val result = client.getReleases(
                "nobody", "norepo"
            )

            assertTrue(result.isFailure)
        }
}
