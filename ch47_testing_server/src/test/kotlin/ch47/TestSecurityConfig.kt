package ch47

import org.springframework.boot.test.context
    .TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.jose.jws
    .MacAlgorithm
import org.springframework.security.oauth2.jwt
    .JwtDecoder
import org.springframework.security.oauth2.jwt
    .NimbusJwtDecoder
import javax.crypto.spec.SecretKeySpec

@TestConfiguration
class TestSecurityConfig {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        val key = SecretKeySpec(
            "test-secret-key-must-be-32-bytes!"
                .toByteArray(),
            "HmacSHA256"
        )
        return NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
    }
}
