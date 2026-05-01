package ch06

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0,
    @SerialName("public_gists")
    val publicGists: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
data class RepoOwner(
    val login: String,
    val id: Long
)

@Serializable
data class Repo(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val owner: RepoOwner,
    val private: Boolean = false,
    val description: String? = null,
    val fork: Boolean = false,
    val url: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    @SerialName("watchers_count")
    val watchersCount: Int = 0,
    @SerialName("forks_count")
    val forksCount: Int = 0,
    val language: String? = null,
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0,
    @SerialName("default_branch")
    val defaultBranch: String = "main",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)
