package ch43.exception

sealed class LibraryException(
    msg: String
) : RuntimeException(msg) {
    class BookNotFoundException(id: Long)
        : LibraryException(
            "Book $id not found"
        )
    class AuthorNotFoundException(id: Long)
        : LibraryException(
            "Author $id not found"
        )
    class BookNotAvailableException(id: Long)
        : LibraryException(
            "Book $id is not available"
        )
    class DuplicateIsbnException(isbn: String)
        : LibraryException(
            "ISBN $isbn already exists"
        )
}
