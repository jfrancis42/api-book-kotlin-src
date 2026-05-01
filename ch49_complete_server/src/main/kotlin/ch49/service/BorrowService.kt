package ch49.service

import ch49.dto.BorrowDto
import ch49.exception.LibraryException
import ch49.model.Borrow
import ch49.repository.BookRepository
import ch49.repository.BorrowRepository
import org.springframework.stereotype.Service

@Service
class BorrowService(
    private val bookRepository: BookRepository,
    private val borrowRepository: BorrowRepository
) {
    fun borrow(
        bookId: Long,
        userId: String
    ): BorrowDto {
        val book = bookRepository.findById(bookId)
            .orElseThrow {
                LibraryException
                    .BookNotFoundException(bookId)
            }
        if (!book.available) {
            throw LibraryException
                .BookNotAvailableException(bookId)
        }
        bookRepository.save(
            book.copy(available = false)
        )
        val borrow = borrowRepository.save(
            Borrow(
                book = book,
                userId = userId,
                borrowedAt = java.time.Instant.now()
            )
        )
        return BorrowDto.from(borrow)
    }

    fun returnBook(borrowId: Long): BorrowDto {
        val borrow = borrowRepository
            .findById(borrowId)
            .orElseThrow {
                LibraryException
                    .BorrowNotFoundException(
                        borrowId
                    )
            }
        val updated = borrowRepository.save(
            borrow.copy(
                returnedAt =
                    java.time.Instant.now()
            )
        )
        borrow.book?.let { book ->
            bookRepository.save(
                book.copy(available = true)
            )
        }
        return BorrowDto.from(updated)
    }
}
