package ch41.repository

import ch41.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>
