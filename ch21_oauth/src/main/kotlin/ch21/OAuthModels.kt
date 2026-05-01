package ch21

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCodeResponse(
    @SerialName("device_code")
    val deviceCode: String,
    @SerialName("user_code")
    val userCode: String,
    @SerialName("verification_uri")
    val verificationUri: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val interval: Int
)

@Serializable
data class AccessToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    val scope: String = ""
)

@Serializable
data class OAuthError(
    val error: String,
    @SerialName("error_description")
    val errorDescription: String? = null
)

sealed class DevicePollResult {
    data class Success(val token: AccessToken) :
        DevicePollResult()
    object Pending : DevicePollResult()
    object SlowDown : DevicePollResult()
    data class Failed(val reason: String) :
        DevicePollResult()
}
