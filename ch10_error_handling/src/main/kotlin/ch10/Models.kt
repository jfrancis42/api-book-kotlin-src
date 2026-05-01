package ch10

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0
)

@Serializable
data class GitHubError(
    val message: String,
    @SerialName("documentation_url")
    val documentationUrl: String? = null
)
