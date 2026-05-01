package ch46.service

import ch46.dto.BookDto
import ch46.dto.CreateBookRequest
import ch46.exception.LibraryException
import ch46.model.Book
import ch46.repository.BookRepository
import ch46.repository.BookSpecifications
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
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

    fun search(
        q: String?,
        authorName: String?,
        availableOnly: Boolean
    ): List<BookDto> {
        var spec: Specification<Book>? = null
        if (q != null) {
            spec = (spec ?: Specification.where(null))
                .and(
                    BookSpecifications.titleContains(q)
                )
        }
        if (authorName != null) {
            spec = (spec ?: Specification.where(null))
                .and(
                    BookSpecifications
                        .authorNameContains(authorName)
                )
        }
        if (availableOnly) {
            spec = (spec ?: Specification.where(null))
                .and(BookSpecifications.isAvailable())
        }
        return (
            if (spec != null)
                bookRepository.findAll(spec)
            else
                bookRepository.findAll()
        ).map { it.toDto() }
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
