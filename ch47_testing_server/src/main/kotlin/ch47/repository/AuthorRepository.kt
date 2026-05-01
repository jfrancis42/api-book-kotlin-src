package ch47.repository

import ch47.model.Author
import org.springframework.data.jpa.repository
    .JpaRepository

interface AuthorRepository
    : JpaRepository<Author, Long>
