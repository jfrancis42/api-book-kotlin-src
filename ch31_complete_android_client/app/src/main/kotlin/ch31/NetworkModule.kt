package ch31

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader(
                        "User-Agent",
                        "suspend-disbelief-book/1.0"
                    )
                    .build()
            )
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()

    val service: GitHubService =
        retrofit.create(GitHubService::class.java)

    val repository: GitHubRepository =
        GitHubRepository(service)
}
