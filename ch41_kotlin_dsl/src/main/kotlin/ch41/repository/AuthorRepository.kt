package ch41.repository

import ch41.model.Author
import org.springframework.data.jpa.repository.JpaRepository

interface AuthorRepository : JpaRepository<Author, Long>
