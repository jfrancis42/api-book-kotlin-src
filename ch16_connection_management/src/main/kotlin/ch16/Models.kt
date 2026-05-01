package ch16

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
data class Repo(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    val description: String? = null
)

@Serializable
data class GitHubError(val message: String)
