package ch45.repository

import ch45.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BookRepository :
    JpaRepository<Book, Long>,
    JpaSpecificationExecutor<Book>
