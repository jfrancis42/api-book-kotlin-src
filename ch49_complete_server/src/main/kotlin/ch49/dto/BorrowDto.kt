package ch49.dto

import ch49.model.Borrow

data class BorrowDto(
    val id: Long = 0,
    val bookId: Long = 0,
    val userId: String = "",
    val borrowedAt: java.time.Instant =
        java.time.Instant.now(),
    val returnedAt: java.time.Instant? = null
) {
    companion object {
        fun from(borrow: Borrow) = BorrowDto(
            id = borrow.id,
            bookId = borrow.book?.id ?: 0,
            userId = borrow.userId,
            borrowedAt = borrow.borrowedAt,
            returnedAt = borrow.returnedAt
        )
    }
}

data class CreateBorrowRequest(
    val userId: String = ""
)
