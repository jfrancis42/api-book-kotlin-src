package ch18

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
data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String? = null,
    val state: String = "open"
)

@Serializable
data class IssueBody(
    val title: String,
    val body: String? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)

@Serializable
data class GitHubError(val message: String)
