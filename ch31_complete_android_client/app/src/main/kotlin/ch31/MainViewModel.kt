package ch31

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    fun load(login: String) {
        viewModelScope.launch {
            _state.value = MainState()
            val userDeferred =
                async { repository.getUser(login) }
            val reposDeferred =
                async { repository.getUserRepos(login) }

            val user = userDeferred.await()
            val repos = reposDeferred.await()

            _state.value = MainState(
                user = when (user) {
                    is ApiResult.Success ->
                        UiState.Success(user.data)
                    is ApiResult.HttpError ->
                        UiState.Error(
                            "HTTP ${user.status}"
                        )
                    is ApiResult.NetworkError ->
                        UiState.Error(
                            user.cause.message
                                ?: "Network error"
                        )
                },
                repos = when (repos) {
                    is ApiResult.Success ->
                        UiState.Success(repos.data)
                    is ApiResult.HttpError ->
                        UiState.Error(
                            "HTTP ${repos.status}"
                        )
                    is ApiResult.NetworkError ->
                        UiState.Error(
                            repos.cause.message
                                ?: "Network error"
                        )
                }
            )
        }
    }
}
