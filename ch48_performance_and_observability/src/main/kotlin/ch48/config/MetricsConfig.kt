package ch48.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation
    .Configuration
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Counter

@Configuration
class MetricsConfig {
    @Bean
    fun booksBorrowedCounter(
        registry: MeterRegistry
    ): Counter = Counter.builder("library.borrows")
        .description("Books borrowed")
        .register(registry)
}
