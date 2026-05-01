package ch31

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        setContentView(textView)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T :
                    androidx.lifecycle.ViewModel>
                    create(
                        modelClass: Class<T>
                    ): T = MainViewModel(
                    NetworkModule.repository
                ) as T
            }
        )[MainViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.state.collect { state ->
                    render(textView, state)
                }
            }
        }

        viewModel.load("torvalds")
    }

    private fun render(
        view: TextView,
        state: MainState
    ) {
        val userText = when (val u = state.user) {
            is UiState.Loading -> "Loading user..."
            is UiState.Success ->
                "Login: ${u.data.login}\n" +
                "Repos: ${u.data.publicRepos}"
            is UiState.Error -> "User error: ${u.message}"
        }
        val repoText = when (val r = state.repos) {
            is UiState.Loading -> "Loading repos..."
            is UiState.Success ->
                "Repo count: ${r.data.size}"
            is UiState.Error ->
                "Repos error: ${r.message}"
        }
        view.text = "$userText\n$repoText"
    }
}
