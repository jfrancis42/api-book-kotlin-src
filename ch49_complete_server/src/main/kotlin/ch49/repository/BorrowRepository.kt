package ch49.repository

import ch49.model.Borrow
import org.springframework.data.jpa.repository
    .JpaRepository

interface BorrowRepository
    : JpaRepository<Borrow, Long>
