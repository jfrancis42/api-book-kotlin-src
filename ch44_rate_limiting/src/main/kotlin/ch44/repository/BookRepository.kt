package ch44.repository

import ch44.model.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>
