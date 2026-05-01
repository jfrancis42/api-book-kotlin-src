package ch47.service

import ch47.dto.BookDto
import ch47.dto.CreateBookRequest
import ch47.exception.LibraryException
import ch47.model.Book
import ch47.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    fun findAll(
        pageable: Pageable
    ): Page<BookDto> =
        bookRepository.findAll(pageable)
            .map { it.toDto() }

    fun findById(id: Long): BookDto? =
        bookRepository.findById(id)
            .map { it.toDto() }
            .orElse(null)

    fun create(
        req: CreateBookRequest
    ): BookDto {
        val book = Book(
            title = req.title,
            isbn = req.isbn,
            year = req.year
        )
        return bookRepository.save(book).toDto()
    }

    fun delete(id: Long) {
        if (!bookRepository.existsById(id)) {
            throw LibraryException
                .BookNotFoundException(id)
        }
        bookRepository.deleteById(id)
    }

    private fun Book.toDto() = BookDto(
        id = id,
        title = title,
        author = author?.name ?: "",
        isbn = isbn,
        year = year,
        available = available
    )
}
