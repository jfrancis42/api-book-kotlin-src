package ch22

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushEvent(
    val ref: String,
    val repository: WebhookRepo,
    val pusher: Pusher,
    val commits: List<Commit> = emptyList()
)

@Serializable
data class WebhookRepo(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String
)

@Serializable
data class Pusher(
    val name: String,
    val email: String? = null
)

@Serializable
data class Commit(
    val id: String,
    val message: String,
    val author: CommitAuthor
)

@Serializable
data class CommitAuthor(
    val name: String,
    val email: String
)
