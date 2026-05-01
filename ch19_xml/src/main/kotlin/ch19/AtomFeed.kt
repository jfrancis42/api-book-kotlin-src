package ch19

data class AtomEntry(
    val id: String,
    val title: String,
    val updated: String,
    val link: String,
    val content: String?
)

data class AtomFeed(
    val id: String,
    val title: String,
    val updated: String,
    val entries: List<AtomEntry>
)
