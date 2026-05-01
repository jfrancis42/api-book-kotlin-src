package ch14

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

data class Page<T>(
    val items: List<T>,
    val nextUrl: String?,
    val totalCount: Int? = null
)

@Serializable
data class GitHubError(val message: String)
