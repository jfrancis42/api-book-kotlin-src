package ch39.service
import ch39.dto.BookDto
import ch39.dto.CreateBookRequest
import ch39.dto.UpdateBookRequest
import ch39.model.Book
import ch39.repository.AuthorRepository
import ch39.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {
    @Transactional(readOnly = true)
    fun findAll(): List<BookDto> =
        bookRepository.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun findById(id: Long): BookDto? =
        bookRepository.findById(id)
            .orElse(null)?.toDto()

    @Transactional
    fun create(req: CreateBookRequest): BookDto {
        val author = authorRepository
            .findById(req.authorId)
            .orElseThrow {
                NoSuchElementException(
                    "Author ${req.authorId} not found"
                )
            }
        val book = bookRepository.save(
            Book(
                title = req.title,
                author = author,
                isbn = req.isbn,
                year = req.year
            )
        )
        return book.toDto()
    }

    @Transactional
    fun update(
        id: Long,
        req: UpdateBookRequest
    ): BookDto? {
        val book = bookRepository.findById(id)
            .orElse(null) ?: return null
        val updated = book.copy(
            title = req.title ?: book.title,
            isbn = req.isbn ?: book.isbn,
            year = req.year ?: book.year,
            available = req.available
                ?: book.available
        )
        return bookRepository.save(updated).toDto()
    }

    @Transactional
    fun delete(id: Long): Boolean {
        if (!bookRepository.existsById(id))
            return false
        bookRepository.deleteById(id)
        return true
    }

    private fun Book.toDto() = BookDto(
        id = id,
        title = title,
        authorName = author?.name,
        isbn = isbn,
        year = year,
        available = available
    )
}
