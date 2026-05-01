package ch46.webhook

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class WebhookDispatcher(
    private val restTemplate: RestTemplate =
        RestTemplate()
) {
    private val log = LoggerFactory.getLogger(
        WebhookDispatcher::class.java
    )
    private val subscribers =
        mutableListOf<String>()

    fun subscribe(url: String) {
        subscribers.add(url)
    }

    fun dispatch(event: WebhookEvent) {
        subscribers.forEach { url ->
            try {
                restTemplate.postForEntity(
                    url, event, String::class.java
                )
            } catch (e: Exception) {
                log.warn(
                    "Webhook delivery failed" +
                    " to $url: ${e.message}"
                )
            }
        }
    }
}
