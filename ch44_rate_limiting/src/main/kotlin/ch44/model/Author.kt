package ch44.model

import jakarta.persistence.*

@Entity @Table(name = "authors")
data class Author(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    val id: Long = 0,
    val name: String = "",
    val bio: String = ""
)
