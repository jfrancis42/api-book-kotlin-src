package ch46.ratelimit

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitInterceptor : HandlerInterceptor {

    private val buckets =
        ConcurrentHashMap<String, Bucket>()

    private fun getBucket(key: String): Bucket =
        buckets.getOrPut(key) {
            Bucket.builder()
                .addLimit(
                    Bandwidth.builder()
                        .capacity(100)
                        .refillGreedy(
                            100,
                            Duration.ofMinutes(1)
                        )
                        .build()
                )
                .build()
        }

    private fun clientKey(
        request: HttpServletRequest
    ): String {
        val forwarded =
            request.getHeader("X-Forwarded-For")
        return forwarded?.split(",")
            ?.firstOrNull()?.trim()
            ?: request.remoteAddr
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val key = clientKey(request)
        val bucket = getBucket(key)
        return if (bucket.tryConsume(1)) {
            true
        } else {
            response.status =
                HttpStatus.TOO_MANY_REQUESTS.value()
            response.setHeader(
                "Retry-After", "60"
            )
            response.writer.write(
                """{"status":429,""" +
                """"code":"RATE_LIMIT_EXCEEDED",""" +
                """"message":"Too many requests"}"""
            )
            false
        }
    }
}
