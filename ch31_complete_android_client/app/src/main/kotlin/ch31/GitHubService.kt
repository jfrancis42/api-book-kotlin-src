package ch31

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): Response<User>

    @GET("users/{login}/repos")
    suspend fun getUserRepos(
        @Path("login") login: String,
        @Query("per_page") perPage: Int = 30
    ): Response<List<Repo>>
}
