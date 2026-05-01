package ch49.repository

import ch49.model.Author
import org.springframework.data.jpa.repository
    .JpaRepository

interface AuthorRepository
    : JpaRepository<Author, Long>
