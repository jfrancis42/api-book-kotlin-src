package ch49.model

import jakarta.persistence.*
import org.springframework.data.annotation
    .CreatedDate
import org.springframework.data.annotation
    .LastModifiedDate
import org.springframework.data.jpa.domain.support
    .AuditingEntityListener

@Entity @Table(name = "books")
@EntityListeners(AuditingEntityListener::class)
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
    val available: Boolean = true,
    @Version
    val version: Long = 0,
    @CreatedDate
    val createdAt: java.time.Instant? = null,
    @LastModifiedDate
    val updatedAt: java.time.Instant? = null
)
