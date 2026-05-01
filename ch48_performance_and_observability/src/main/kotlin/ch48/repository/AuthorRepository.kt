package ch48.repository

import ch48.model.Author
import org.springframework.data.jpa.repository
    .JpaRepository

interface AuthorRepository
    : JpaRepository<Author, Long>
