package submission.learning.storyapp.interfaces.story

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import submission.learning.storyapp.data.preferences.ModelUser
import submission.learning.storyapp.data.response.AddNewStoriesResponse
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.helper.Result

class InsertViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _insertStoryResponse = MediatorLiveData<Result<AddNewStoriesResponse>>()
    val insertStoryResponse: LiveData<Result<AddNewStoriesResponse>> = _insertStoryResponse

    fun addStory(token: String, file: MultipartBody.Part, description: RequestBody) {
        val liveData = userRepository.addStoryApp(token, file, description)
        _insertStoryResponse.addSource(liveData) {
            result -> _insertStoryResponse.value = result
        }
    }

    fun addStoryWithLocation(token: String, file: MultipartBody.Part, description: RequestBody, myLocation: Location?) {
        val liveData = userRepository.addStoryAppWithLocation(token, file, description , myLocation)
        _insertStoryResponse.addSource(liveData) {
                result -> _insertStoryResponse.value = result
        }
    }

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }


}