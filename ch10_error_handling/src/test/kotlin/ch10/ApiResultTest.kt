package ch10

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiResultTest {

    @Test
    fun `Success contains data`() {
        val result = ApiResult.Success("hello")
        assertTrue(result.isSuccess())
        assertEquals("hello", result.data)
    }

    @Test
    fun `HttpError contains status and message`() {
        val result = ApiResult.HttpError(404, "Not Found")
        assertEquals(404, result.status)
        assertEquals("Not Found", result.message)
    }

    @Test
    fun `NetworkError contains cause`() {
        val ex = RuntimeException("connection refused")
        val result = ApiResult.NetworkError(ex)
        assertEquals(ex, result.cause)
    }

    @Test
    fun `map transforms Success data`() {
        val result: ApiResult<Int> =
            ApiResult.Success(42)
        val mapped = result.map { it * 2 }
        assertEquals(84, (mapped as ApiResult.Success).data)
    }

    @Test
    fun `map passes through HttpError`() {
        val result: ApiResult<Int> =
            ApiResult.HttpError(404, "Not Found")
        val mapped = result.map { it * 2 }
        assertTrue(mapped is ApiResult.HttpError)
        assertEquals(404, (mapped as ApiResult.HttpError).status)
    }

    @Test
    fun `map passes through NetworkError`() {
        val ex = RuntimeException("boom")
        val result: ApiResult<Int> =
            ApiResult.NetworkError(ex)
        val mapped = result.map { it * 2 }
        assertTrue(mapped is ApiResult.NetworkError)
    }

    @Test
    fun `getOrNull returns data for Success`() {
        val result = ApiResult.Success("value")
        assertEquals("value", result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for HttpError`() {
        val result = ApiResult.HttpError(500, "Error")
        assertNull(result.getOrNull())
    }

    @Test
    fun `getOrNull returns null for NetworkError`() {
        val result = ApiResult.NetworkError(
            RuntimeException()
        )
        assertNull(result.getOrNull())
    }

    @Test
    fun `isSuccess is true for Success only`() {
        assertTrue(ApiResult.Success("x").isSuccess())
        assertTrue(
            !ApiResult.HttpError(404, "nf").isSuccess()
        )
        assertTrue(
            !ApiResult.NetworkError(
                RuntimeException()
            ).isSuccess()
        )
    }
}
