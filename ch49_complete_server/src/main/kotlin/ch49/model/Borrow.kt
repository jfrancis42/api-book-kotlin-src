package ch49.model

import jakarta.persistence.*

@Entity @Table(name = "borrows")
data class Borrow(
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    val book: Book? = null,
    val userId: String = "",
    val borrowedAt: java.time.Instant =
        java.time.Instant.now(),
    val returnedAt: java.time.Instant? = null
)
