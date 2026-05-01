package ch31

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
    private val repos: List<Repo> = emptyList(),
    private val statusCode: Int = 200
) : GitHubService {
    override suspend fun getUser(
        login: String
    ): Response<User> =
        if (statusCode == 200 && user != null) {
            Response.success(user)
        } else {
            Response.error(
                statusCode,
                okhttp3.ResponseBody.create(null, "")
            )
        }

    override suspend fun getUserRepos(
        login: String,
        perPage: Int
    ): Response<List<Repo>> =
        if (statusCode == 200) {
            Response.success(repos)
        } else {
            Response.error(
                statusCode,
                okhttp3.ResponseBody.create(null, "")
            )
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
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
    fun `load emits Success for user and repos`() =
        runTest {
            val user = User(login = "alice", id = 1)
            val repos = listOf(
                Repo(
                    id = 1,
                    name = "project",
                    fullName = "alice/project"
                )
            )
            val vm = MainViewModel(
                GitHubRepository(
                    FakeGitHubService(
                        user = user,
                        repos = repos
                    )
                )
            )

            vm.load("alice")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = vm.state.value
            assertTrue(state.user is UiState.Success)
            assertTrue(state.repos is UiState.Success)
            assertEquals(
                "alice",
                (state.user as UiState.Success)
                    .data.login
            )
            assertEquals(
                1,
                (state.repos as UiState.Success)
                    .data.size
            )
        }

    @Test
    fun `load handles 404 gracefully`() = runTest {
        val vm = MainViewModel(
            GitHubRepository(
                FakeGitHubService(statusCode = 404)
            )
        )

        vm.load("nobody")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.user is UiState.Error)
        assertTrue(state.repos is UiState.Error)
    }

    @Test
    fun `both requests run in parallel`() = runTest {
        var userCallCount = 0
        var repoCallCount = 0
        val service = object : GitHubService {
            override suspend fun getUser(
                login: String
            ): Response<User> {
                userCallCount++
                return Response.success(
                    User(login = "alice", id = 1)
                )
            }
            override suspend fun getUserRepos(
                login: String,
                perPage: Int
            ): Response<List<Repo>> {
                repoCallCount++
                return Response.success(emptyList())
            }
        }
        val vm = MainViewModel(GitHubRepository(service))

        vm.load("alice")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, userCallCount)
        assertEquals(1, repoCallCount)
    }
}
