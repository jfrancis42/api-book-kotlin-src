package ch28

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeGitHubService(
    private val user: User? = null,
    private val statusCode: Int = 200
) : GitHubService {
    override suspend fun getUser(
        login: String
    ): Response<User> = if (statusCode == 200) {
        Response.success(user!!)
    } else {
        Response.error(
            statusCode,
            okhttp3.ResponseBody.create(null, "")
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUser emits Success state`() = runTest {
        val user = User(login = "alice", id = 1)
        val vm = UserViewModel(
            UserRepository(FakeGitHubService(user = user))
        )

        vm.loadUser("alice")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(
            "alice",
            (state as UiState.Success).data.login
        )
    }

    @Test
    fun `loadUser emits Error on HttpError`() = runTest {
        val vm = UserViewModel(
            UserRepository(
                FakeGitHubService(statusCode = 404)
            )
        )

        vm.loadUser("nobody")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is UiState.Error)
    }
}
