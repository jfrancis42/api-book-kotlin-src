package ch25

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): User

    @GET("users/{login}/repos")
    suspend fun getUserRepos(
        @Path("login") login: String,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String? = null
    ): List<User>
}
