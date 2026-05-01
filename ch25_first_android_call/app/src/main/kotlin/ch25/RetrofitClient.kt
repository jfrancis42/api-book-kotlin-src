package ch25

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun buildRetrofit(
    baseUrl: String = "https://api.github.com/",
    token: String? = null
): Retrofit {
    val okHttp = OkHttpClient.Builder()
        .apply {
            if (token != null) {
                addInterceptor { chain ->
                    val req = chain.request()
                        .newBuilder()
                        .addHeader(
                            "Authorization",
                            "Bearer $token"
                        )
                        .addHeader(
                            "User-Agent",
                            "suspend-disbelief-book/1.0"
                        )
                        .build()
                    chain.proceed(req)
                }
            }
        }
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttp)
        .addConverterFactory(
            json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .build()
}

val gitHubService: GitHubService =
    buildRetrofit().create(GitHubService::class.java)
