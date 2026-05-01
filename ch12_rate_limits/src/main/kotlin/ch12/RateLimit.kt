package ch12

import io.ktor.client.statement.*

data class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val reset: Long,
    val used: Int
)

data class RateLimitedResponse<T>(
    val data: T,
    val rateLimit: RateLimitInfo?
)

fun HttpResponse.parseRateLimit(): RateLimitInfo? {
    val limit = headers["X-RateLimit-Limit"]
        ?.toIntOrNull() ?: return null
    val remaining = headers["X-RateLimit-Remaining"]
        ?.toIntOrNull() ?: return null
    val reset = headers["X-RateLimit-Reset"]
        ?.toLongOrNull() ?: return null
    val used = headers["X-RateLimit-Used"]
        ?.toIntOrNull() ?: 0
    return RateLimitInfo(limit, remaining, reset, used)
}
