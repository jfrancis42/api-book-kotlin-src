package ch47.repository

import ch47.model.Book
import org.springframework.data.jpa.repository
    .JpaRepository
import org.springframework.data.jpa.repository
    .JpaSpecificationExecutor

interface BookRepository
    : JpaRepository<Book, Long>,
      JpaSpecificationExecutor<Book>
