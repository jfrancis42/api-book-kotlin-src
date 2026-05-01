package ch19

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource

private const val ATOM_NS =
    "http://www.w3.org/2005/Atom"

class AtomClient(
    private val baseUrl: String =
        "https://github.com"
) {
    private val http = HttpClient(OkHttp) {
        install(DefaultRequest) {
            headers {
                append(
                    HttpHeaders.UserAgent,
                    "suspend-disbelief-book/1.0"
                )
                append(
                    HttpHeaders.Accept,
                    "application/atom+xml,application/xml"
                )
            }
        }
        expectSuccess = false
    }

    suspend fun getReleases(
        owner: String,
        repo: String
    ): Result<AtomFeed> = try {
        val response = http.get(
            "$baseUrl/$owner/$repo/releases.atom"
        )
        if (response.status.isSuccess()) {
            val xml = response.bodyAsText()
            Result.success(parseAtomFeed(xml))
        } else {
            Result.failure(
                RuntimeException(
                    "HTTP ${response.status.value}"
                )
            )
        }
    } catch (e: IOException) {
        Result.failure(e)
    }

    fun close() { http.close() }
}

fun parseAtomFeed(xml: String): AtomFeed {
    val factory = DocumentBuilderFactory.newInstance()
    factory.isNamespaceAware = true
    val builder = factory.newDocumentBuilder()
    val doc = builder.parse(
        InputSource(StringReader(xml))
    )
    val root = doc.documentElement

    val id = root.firstTextContent("id", ATOM_NS)
        ?: ""
    val title = root.firstTextContent(
        "title", ATOM_NS
    ) ?: ""
    val updated = root.firstTextContent(
        "updated", ATOM_NS
    ) ?: ""

    val entryNodes = root.getElementsByTagNameNS(
        ATOM_NS, "entry"
    )
    val entries = (0 until entryNodes.length).map { i ->
        val entry = entryNodes.item(i) as Element
        AtomEntry(
            id = entry.firstTextContent(
                "id", ATOM_NS
            ) ?: "",
            title = entry.firstTextContent(
                "title", ATOM_NS
            ) ?: "",
            updated = entry.firstTextContent(
                "updated", ATOM_NS
            ) ?: "",
            link = entry
                .getElementsByTagNameNS(ATOM_NS, "link")
                .item(0)
                ?.let { it as? Element }
                ?.getAttribute("href") ?: "",
            content = entry.firstTextContent(
                "content", ATOM_NS
            )
        )
    }

    return AtomFeed(
        id = id,
        title = title,
        updated = updated,
        entries = entries
    )
}

private fun Element.firstTextContent(
    localName: String,
    namespace: String
): String? {
    val nodes: NodeList =
        getElementsByTagNameNS(namespace, localName)
    if (nodes.length == 0) return null
    return nodes.item(0)?.textContent?.trim()
}
