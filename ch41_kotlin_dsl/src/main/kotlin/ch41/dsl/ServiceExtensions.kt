package ch41.dsl

import ch41.exception.LibraryException

fun <T> T?.orNotFound(id: Long): T =
    this ?: throw LibraryException
        .BookNotFoundException(id)
