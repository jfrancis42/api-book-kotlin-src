package ch28

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object UserViewModelFactory :
    ViewModelProvider.Factory {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .addHeader(
                                "User-Agent",
                                "suspend-disbelief/1.0"
                            )
                            .build()
                    )
                }
                .build()
        )
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    private val service =
        retrofit.create(GitHubService::class.java)

    private val repository = UserRepository(service)

    fun create(context: Context): UserViewModel =
        UserViewModel(repository)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T = UserViewModel(repository) as T
}
