package ch46.webhook

data class WebhookEvent(
    val type: String,
    val bookId: Long,
    val userId: String? = null,
    val timestamp: Long =
        System.currentTimeMillis()
)
