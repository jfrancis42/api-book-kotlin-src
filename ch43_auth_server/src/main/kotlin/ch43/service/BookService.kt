package ch43.service

import ch43.dto.BookDto
import ch43.dto.CreateBookRequest
import ch43.exception.LibraryException
import ch43.model.Book
import ch43.repository.BookRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    fun findAll(): List<BookDto> =
        bookRepository.findAll().map { it.toDto() }

    fun findAll(pageable: Pageable): Page<BookDto> =
        bookRepository.findAll(pageable)
            .map { it.toDto() }

    fun findById(id: Long): BookDto =
        bookRepository.findById(id)
            .orElseThrow {
                LibraryException.BookNotFoundException(id)
            }
            .toDto()

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
