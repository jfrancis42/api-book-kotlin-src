package ch28

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: UserRepository

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
        repository = UserRepository(
            retrofit.create(GitHubService::class.java)
        )
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `getUser returns Success on 200`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody(
                    """{"login":"alice","id":1}"""
                )
        )

        val result = repository.getUser("alice")

        assertTrue(result is ApiResult.Success)
        assertEquals(
            "alice",
            (result as ApiResult.Success).data.login
        )
    }

    @Test
    fun `getUser returns HttpError on 404`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(404)
        )

        val result = repository.getUser("nobody")

        assertTrue(result is ApiResult.HttpError)
        assertEquals(
            404,
            (result as ApiResult.HttpError).status
        )
    }

    @Test
    fun `getUser sends correct path`() = runTest {
        server.enqueue(
            MockResponse()
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .setBody("""{"login":"bob","id":2}""")
        )

        repository.getUser("bob")

        val request = server.takeRequest()
        assertEquals("/users/bob", request.path)
    }
}
