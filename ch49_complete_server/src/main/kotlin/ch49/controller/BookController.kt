package ch49.controller

import ch49.dto.BookDto
import ch49.dto.BorrowDto
import ch49.dto.CreateBookRequest
import ch49.dto.UpdateBookRequest
import ch49.exception.LibraryException
import ch49.service.BookService
import ch49.service.BorrowService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService,
    private val borrowService: BorrowService
) {

    @GetMapping
    fun listBooks(
        @RequestParam(required = false)
        q: String?,
        @RequestParam(required = false)
        available: Boolean?,
        @PageableDefault(size = 20)
        pageable: Pageable
    ): Page<BookDto> =
        bookService.search(q, available, pageable)

    @GetMapping("/{id}")
    fun getBook(
        @PathVariable id: Long
    ): ResponseEntity<BookDto> {
        val book = bookService.findById(id)
            ?: return ResponseEntity
                .notFound().build()
        return ResponseEntity.ok(book)
    }

    @PostMapping
    fun createBook(
        @Valid
        @RequestBody req: CreateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.create(req)
        return ResponseEntity.status(201).body(book)
    }

    @PatchMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody req: UpdateBookRequest
    ): ResponseEntity<BookDto> {
        val book = bookService.update(id, req)
        return ResponseEntity.ok(book)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        bookService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/borrow")
    fun borrowBook(
        @PathVariable id: Long,
        @RequestParam(
            defaultValue = "anonymous"
        ) userId: String
    ): ResponseEntity<BorrowDto> {
        val borrow = borrowService.borrow(
            id, userId
        )
        return ResponseEntity.ok(borrow)
    }

    @PostMapping("/{id}/return")
    fun returnBook(
        @PathVariable id: Long,
        @RequestParam borrowId: Long
    ): ResponseEntity<BorrowDto> {
        val borrow = borrowService.returnBook(
            borrowId
        )
        return ResponseEntity.ok(borrow)
    }
}
