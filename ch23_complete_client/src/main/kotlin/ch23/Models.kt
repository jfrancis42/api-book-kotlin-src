package ch23

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0,
    @SerialName("private_gists")
    val privateGists: Int? = null
)

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    val description: String? = null,
    val private: Boolean = false
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
data class CreateIssueRequest(
    val title: String,
    val body: String? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)

@Serializable
data class SearchResult(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<Repo>
)

@Serializable
data class GitHubError(val message: String)

data class Page<T>(
    val items: List<T>,
    val nextUrl: String?,
    val totalCount: Int? = null
)

data class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val reset: Long,
    val used: Int
)
