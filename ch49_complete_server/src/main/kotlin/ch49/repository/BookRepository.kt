package ch49.repository

import ch49.model.Book
import org.springframework.data.jpa.repository
    .JpaRepository
import org.springframework.data.jpa.repository
    .JpaSpecificationExecutor

interface BookRepository
    : JpaRepository<Book, Long>,
      JpaSpecificationExecutor<Book>
