package ch38.service
import ch38.dto.BookDto
import ch38.dto.CreateBookRequest
import ch38.model.Book
import ch38.repository.AuthorRepository
import ch38.repository.BookRepository
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

    private fun Book.toDto() = BookDto(
        id = id,
        title = title,
        authorName = author?.name,
        isbn = isbn,
        year = year,
        available = available
    )
}
