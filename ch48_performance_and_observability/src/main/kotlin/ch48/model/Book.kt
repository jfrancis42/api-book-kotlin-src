package ch48.model

import jakarta.persistence.*

@Entity @Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    val id: Long = 0,
    val title: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    val author: Author? = null,
    val isbn: String = "",
    @Column(name = "published_year")
    val year: Int = 0,
    val available: Boolean = true
)
