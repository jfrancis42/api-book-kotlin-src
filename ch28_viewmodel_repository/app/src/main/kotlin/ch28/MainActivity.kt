package ch28

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        setContentView(textView)

        viewModel = UserViewModelFactory
            .create(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(textView, state)
                }
            }
        }

        viewModel.loadUser("torvalds")
    }

    private fun render(
        view: TextView,
        state: UiState<User>
    ) {
        view.text = when (state) {
            is UiState.Loading -> "Loading..."
            is UiState.Success ->
                "Login: ${state.data.login}\n" +
                "Repos: ${state.data.publicRepos}"
            is UiState.Error ->
                "Error: ${state.message}"
        }
    }
}
