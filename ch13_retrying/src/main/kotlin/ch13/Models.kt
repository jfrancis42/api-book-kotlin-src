package ch13

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null
)

@Serializable
data class GitHubError(val message: String)
