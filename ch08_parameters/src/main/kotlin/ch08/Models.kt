package ch08

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
    val description: String? = null,
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    val language: String? = null,
    @SerialName("forks_count")
    val forksCount: Int = 0
)

@Serializable
data class SearchResult(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean = false,
    val items: List<Repo>
)

data class SearchParams(
    val language: String? = null,
    val sort: String? = null,
    val order: String? = null,
    val perPage: Int? = null,
    val page: Int? = null
)
