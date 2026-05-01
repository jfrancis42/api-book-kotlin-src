package ch07

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val login: String,
    val id: Long,
    val name: String? = null,
    val email: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0,
    val followers: Int = 0,
    @SerialName("private_gists")
    val privateGists: Int? = null,
    @SerialName("total_private_repos")
    val totalPrivateRepos: Int? = null
)
