package ch28

import retrofit2.HttpException
import java.io.IOException

class UserRepository(
    private val service: GitHubService
) {
    suspend fun getUser(
        login: String
    ): ApiResult<User> = try {
        val response = service.getUser(login)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.HttpError(
                    response.code(),
                    "Empty response body"
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
}
