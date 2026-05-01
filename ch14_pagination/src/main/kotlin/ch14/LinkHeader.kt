package ch14

private val LINK_NEXT_RE = Regex(
    """<([^>]+)>\s*;\s*rel="next""""
)

fun parseLinkNext(linkHeader: String?): String? =
    linkHeader?.let { LINK_NEXT_RE.find(it)?.groupValues?.get(1) }
