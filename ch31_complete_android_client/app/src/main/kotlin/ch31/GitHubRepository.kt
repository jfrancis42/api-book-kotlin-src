package ch31

import retrofit2.HttpException
import java.io.IOException

class GitHubRepository(
    private val service: GitHubService
) {
    suspend fun <T> apiCall(
        call: suspend () -> retrofit2.Response<T>
    ): ApiResult<T> = try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.HttpError(
                    response.code(),
                    "Empty body"
                )
            }
        } else {
            ApiResult.HttpError(
                response.code(),
                response.message()
            )
        }
    } catch (e: HttpException) {
        ApiResult.HttpError(e.code(), e.message())
    } catch (e: IOException) {
        ApiResult.NetworkError(e)
    }

    suspend fun getUser(
        login: String
    ): ApiResult<User> = apiCall {
        service.getUser(login)
    }

    suspend fun getUserRepos(
        login: String
    ): ApiResult<List<Repo>> = apiCall {
        service.getUserRepos(login)
    }
}
