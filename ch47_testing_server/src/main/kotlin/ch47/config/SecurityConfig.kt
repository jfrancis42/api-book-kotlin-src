package ch47.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation
    .Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation
    .method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation
    .web.builders.HttpSecurity
import org.springframework.security.config.http
    .SessionCreationPolicy
import org.springframework.security.web
    .SecurityFilterChain
import org.springframework.security.config.Customizer

@Configuration
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { sm ->
                sm.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/books",
                        "/api/books/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(
                    Customizer.withDefaults()
                )
            }
            .build()
}
