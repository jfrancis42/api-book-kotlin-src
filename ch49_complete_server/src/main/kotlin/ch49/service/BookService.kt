package ch49.service

import ch49.dto.BookDto
import ch49.dto.CreateBookRequest
import ch49.dto.UpdateBookRequest
import ch49.exception.LibraryException
import ch49.model.Book
import ch49.repository.AuthorRepository
import ch49.repository.BookRepository
import ch49.repository.BookSpecifications
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain
    .Specification
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {
    fun findAll(
        pageable: Pageable
    ): Page<BookDto> =
        bookRepository.findAll(pageable)
            .map { BookDto.from(it) }

    fun findById(id: Long): BookDto? =
        bookRepository.findById(id)
            .map { BookDto.from(it) }
            .orElse(null)

    fun search(
        term: String?,
        available: Boolean?,
        pageable: Pageable
    ): Page<BookDto> {
        var spec: Specification<Book>? = null
        if (term != null)
            spec = (spec?.and(
                BookSpecifications
                    .titleContains(term)
            ) ?: BookSpecifications
                .titleContains(term))
        if (available != null)
            spec = (spec?.and(
                BookSpecifications
                    .availableIs(available)
            ) ?: BookSpecifications
                .availableIs(available))
        return if (spec != null)
            bookRepository.findAll(
                spec, pageable
            ).map { BookDto.from(it) }
        else
            bookRepository.findAll(pageable)
                .map { BookDto.from(it) }
    }

    fun create(
        req: CreateBookRequest
    ): BookDto {
        val author = req.author_id?.let {
            authorRepository.findById(it)
                .orElseThrow {
                    LibraryException
                        .AuthorNotFoundException(it)
                }
        }
        val book = Book(
            title = req.title,
            author = author,
            isbn = req.isbn,
            year = req.year
        )
        return BookDto.from(
            bookRepository.save(book)
        )
    }

    fun update(
        id: Long,
        req: UpdateBookRequest
    ): BookDto {
        val book = bookRepository.findById(id)
            .orElseThrow {
                LibraryException
                    .BookNotFoundException(id)
            }
        val updated = book.copy(
            title = req.title ?: book.title,
            isbn = req.isbn ?: book.isbn,
            year = req.year ?: book.year
        )
        return BookDto.from(
            bookRepository.save(updated)
        )
    }

    fun delete(id: Long) {
        if (!bookRepository.existsById(id)) {
            throw LibraryException
                .BookNotFoundException(id)
        }
        bookRepository.deleteById(id)
    }
}
