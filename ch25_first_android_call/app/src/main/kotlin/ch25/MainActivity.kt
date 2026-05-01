package ch25

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        setContentView(textView)

        lifecycleScope.launch {
            try {
                val user = gitHubService.getUser(
                    "torvalds"
                )
                textView.text =
                    "Login: ${user.login}\n" +
                    "Repos: ${user.publicRepos}"
            } catch (e: Exception) {
                textView.text = "Error: ${e.message}"
            }
        }
    }
}
