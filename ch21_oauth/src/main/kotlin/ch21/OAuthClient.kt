package ch21

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.IOException

private const val GITHUB_OAUTH_URL =
    "https://github.com/login/oauth"

class OAuthClient(
    private val clientId: String,
    private val oauthBaseUrl: String =
        GITHUB_OAUTH_URL
) {
    private val http = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        expectSuccess = false
    }

    suspend fun requestDeviceCode(
        scope: String = "repo"
    ): Result<DeviceCodeResponse> = try {
        val response = http.post(
            "$oauthBaseUrl/device/code"
        ) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                mapOf(
                    "client_id" to clientId,
                    "scope" to scope
                )
            )
        }
        if (response.status.isSuccess()) {
            Result.success(
                response.body<DeviceCodeResponse>()
            )
        } else {
            Result.failure(
                RuntimeException(
                    "HTTP ${response.status.value}"
                )
            )
        }
    } catch (e: IOException) {
        Result.failure(e)
    }

    suspend fun pollForToken(
        deviceCode: String
    ): DevicePollResult = try {
        val response = http.post(
            "$oauthBaseUrl/access_token"
        ) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                mapOf(
                    "client_id" to clientId,
                    "device_code" to deviceCode,
                    "grant_type" to
                        "urn:ietf:params:oauth:" +
                        "grant-type:device_code"
                )
            )
        }
        if (!response.status.isSuccess()) {
            DevicePollResult.Failed(
                "HTTP ${response.status.value}"
            )
        } else {
            val bodyText = response.bodyAsText()
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            if (bodyText.contains("access_token")) {
                DevicePollResult.Success(
                    json.decodeFromString<AccessToken>(
                        bodyText
                    )
                )
            } else {
                val err =
                    json.decodeFromString<OAuthError>(
                        bodyText
                    )
                when (err.error) {
                    "authorization_pending" ->
                        DevicePollResult.Pending
                    "slow_down" ->
                        DevicePollResult.SlowDown
                    "expired_token" ->
                        DevicePollResult.Failed(
                            "Device code expired"
                        )
                    "access_denied" ->
                        DevicePollResult.Failed(
                            "User denied access"
                        )
                    else ->
                        DevicePollResult.Failed(
                            err.error
                        )
                }
            }
        }
    } catch (e: IOException) {
        DevicePollResult.Failed(e.message ?: "IO error")
    }

    suspend fun authorizeWithDeviceFlow(
        scope: String = "repo",
        onUserPrompt: (DeviceCodeResponse) -> Unit
    ): Result<AccessToken> {
        val codeResult = requestDeviceCode(scope)
        if (codeResult.isFailure) {
            return Result.failure(
                codeResult.exceptionOrNull()!!
            )
        }
        val deviceCode = codeResult.getOrThrow()
        onUserPrompt(deviceCode)

        var intervalMs = deviceCode.interval * 1000L
        val expiresAt = System.currentTimeMillis() +
            deviceCode.expiresIn * 1000L

        while (System.currentTimeMillis() < expiresAt) {
            delay(intervalMs)
            when (val poll =
                pollForToken(deviceCode.deviceCode)) {
                is DevicePollResult.Success ->
                    return Result.success(poll.token)
                is DevicePollResult.Pending ->
                    continue
                is DevicePollResult.SlowDown -> {
                    intervalMs += 5000L
                    continue
                }
                is DevicePollResult.Failed ->
                    return Result.failure(
                        RuntimeException(poll.reason)
                    )
            }
        }
        return Result.failure(
            RuntimeException("Authorization timed out")
        )
    }

    fun close() { http.close() }
}
