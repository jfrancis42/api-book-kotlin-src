package ch46.repository

import ch46.model.Book
import org.springframework.data.jpa.domain.Specification

object BookSpecifications {

    fun titleContains(
        q: String
    ): Specification<Book> =
        Specification { root, _, cb ->
            cb.like(
                cb.lower(root.get("title")),
                "%${q.lowercase()}%"
            )
        }

    fun authorNameContains(
        name: String
    ): Specification<Book> =
        Specification { root, _, cb ->
            val author = root.join<Any, Any>(
                "author"
            )
            cb.like(
                cb.lower(
                    author.get("name")
                ),
                "%${name.lowercase()}%"
            )
        }

    fun isAvailable(): Specification<Book> =
        Specification { root, _, cb ->
            cb.isTrue(root.get("available"))
        }
}
