package ch28

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<User>>(
            UiState.Loading
        )
    val uiState: StateFlow<UiState<User>> = _uiState

    fun loadUser(login: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = when (
                val result = repository.getUser(login)
            ) {
                is ApiResult.Success ->
                    UiState.Success(result.data)
                is ApiResult.HttpError ->
                    UiState.Error(
                        "HTTP ${result.status}: " +
                            result.message
                    )
                is ApiResult.NetworkError ->
                    UiState.Error(
                        result.cause.message
                            ?: "Network error"
                    )
            }
        }
    }
}
