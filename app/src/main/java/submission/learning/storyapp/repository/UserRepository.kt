package submission.learning.storyapp.repository

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import submission.learning.storyapp.helper.PagingSource
import submission.learning.storyapp.data.preferences.ModelUserPreferences
import submission.learning.storyapp.data.response.RegisterUserResponse
import submission.learning.storyapp.data.retrofit.ApiServices
import submission.learning.storyapp.helper.Result
import kotlinx.coroutines.Dispatchers
import submission.learning.storyapp.data.preferences.ModelUser
import submission.learning.storyapp.data.response.LoginUserResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import submission.learning.storyapp.data.response.AddNewStoriesResponse
import submission.learning.storyapp.data.response.ListStoryItem


class UserRepository private constructor( private val modelUserPreferences: ModelUserPreferences, private val apiServices: ApiServices
){
    fun register (name: String, email: String, password: String):LiveData<Result<RegisterUserResponse>> = liveData(Dispatchers.IO)
    {
        emit(Result.Loading)
        try {
            val response = apiServices.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveSessionLogin(user: ModelUser) {
        modelUserPreferences.saveSessionLogin(user)
    }


    fun login(email: String, password: String): LiveData<Result<LoginUserResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiServices.login(email, password)
            val token = response.loginResult.token
            saveSessionLogin(ModelUser(email, token))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getSession() : Flow<ModelUser> {
        return modelUserPreferences.getSession()
    }





    fun getStories(token: String): LiveData<Result<List<ListStoryItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiServices.getStories(("Bearer $token"))
            val story = response.listStory
            emit(Result.Success(story))

        } catch (e: Exception){
        emit(Result.Error(e.message.toString()))
    }
    }




    fun addStoryApp(token: String, file:MultipartBody.Part ,description: RequestBody): LiveData<Result<AddNewStoriesResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiServices.uploadStories("Bearer $token", file, description)
            emit (Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addStoryAppWithLocation(token: String, file:MultipartBody.Part ,description: RequestBody, myLocation: Location?): LiveData<Result<AddNewStoriesResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = if (myLocation != null) {
                apiServices.uploadStories(
                    "Bearer $token",
                    file, description,
                    myLocation.latitude.toString().toRequestBody("text/plain".toMediaType()),
                    myLocation.longitude.toString().toRequestBody("text/plain".toMediaType())
                )
            } else {
                apiServices.uploadStories("Bearer $token", file, description)
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

    }

    fun getStoriesLocation(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory =  {
                PagingSource("Bearer $token", apiServices)
            }
        ).liveData
    }

    fun getStoriesWithLocation(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiServices.getStoriesWithLocation("Bearer $token")
                val storyList = response.listStory
                emit(Result.Success(storyList))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }








    suspend fun logout() {
        modelUserPreferences.logout()
    }

    companion object {
        @Volatile
        private var instance : UserRepository? = null

        fun getInstance(modelUserPreferences: ModelUserPreferences, apiServices: ApiServices): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(modelUserPreferences, apiServices)
        }.also { instance = it }
    }


}