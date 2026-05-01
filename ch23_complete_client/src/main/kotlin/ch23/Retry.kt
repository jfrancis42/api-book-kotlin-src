package ch23

import kotlinx.coroutines.delay
import kotlin.math.min

suspend fun <T> withRetry(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 1000L,
    maxDelayMs: Long = 30_000L,
    factor: Double = 2.0,
    block: suspend () -> ApiResult<T>
): ApiResult<T> {
    var currentDelay = initialDelayMs
    repeat(maxAttempts - 1) { attempt ->
        val result = block()
        if (!result.isRetryable()) return result
        val jitter = (Math.random() * currentDelay * 0.1)
            .toLong()
        delay(currentDelay + jitter)
        currentDelay = min(
            (currentDelay * factor).toLong(),
            maxDelayMs
        )
    }
    return block()
}
