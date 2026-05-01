package ch28

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): Response<User>
}
