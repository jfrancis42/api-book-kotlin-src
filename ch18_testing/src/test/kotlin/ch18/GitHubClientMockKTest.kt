package ch18

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitHubClientMockKTest {

    private val client = mockk<GitHubClient>()

    @Test
    fun `mock returns Success`() = runTest {
        coEvery { client.getUser("alice") } returns
            ApiResult.Success(
                User(login = "alice", id = 1)
            )

        val result = client.getUser("alice")

        assertTrue(result is ApiResult.Success)
        assertEquals(
            "alice",
            (result as ApiResult.Success).data.login
        )
        coVerify(exactly = 1) { client.getUser("alice") }
    }

    @Test
    fun `mock returns HttpError`() = runTest {
        coEvery {
            client.getUser("nobody")
        } returns ApiResult.HttpError(
            status = 404,
            message = "Not Found"
        )

        val result = client.getUser("nobody")

        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `mock returns NetworkError`() = runTest {
        val cause = IOException("connection refused")
        coEvery {
            client.getUser(any())
        } returns ApiResult.NetworkError(cause)

        val result = client.getUser("alice")

        assertTrue(result is ApiResult.NetworkError)
        assertEquals(
            cause,
            (result as ApiResult.NetworkError).cause
        )
    }

    @Test
    fun `verify no call was made`() = runTest {
        coEvery {
            client.getUser(any())
        } returns ApiResult.Success(
            User(login = "alice", id = 1)
        )

        coVerify(exactly = 0) { client.getUser(any()) }
    }
}
