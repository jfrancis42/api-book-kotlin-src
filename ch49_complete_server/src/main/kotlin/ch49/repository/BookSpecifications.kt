package ch49.repository

import ch49.model.Book
import org.springframework.data.jpa.domain
    .Specification

object BookSpecifications {

    fun titleContains(
        term: String
    ): Specification<Book> =
        Specification { root, _, cb ->
            cb.like(
                cb.lower(root.get("title")),
                "%${term.lowercase()}%"
            )
        }

    fun availableIs(
        value: Boolean
    ): Specification<Book> =
        Specification { root, _, cb ->
            cb.equal(
                root.get<Boolean>("available"),
                value
            )
        }
}
