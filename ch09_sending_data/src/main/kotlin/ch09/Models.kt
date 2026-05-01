package ch09

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String? = null,
    val state: String = "open",
    @SerialName("html_url")
    val htmlUrl: String = ""
)

@Serializable
data class CreateIssueRequest(
    val title: String,
    val body: String? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)

@Serializable
data class UpdateIssueRequest(
    val title: String? = null,
    val body: String? = null,
    val state: String? = null,
    val labels: List<String>? = null
)

@Serializable
data class Label(
    val id: Long,
    val name: String,
    val color: String,
    val description: String? = null
)

@Serializable
data class CreateLabelRequest(
    val name: String,
    val color: String,
    val description: String? = null
)
