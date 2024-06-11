package submission.learning.storyapp.interfaces.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import submission.learning.storyapp.data.preferences.ModelUser
import submission.learning.storyapp.data.response.ListStoryItem
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.helper.Result

class MapsViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _mapsResponse = MediatorLiveData<Result<List<ListStoryItem>>>()
    val mapsResponse: LiveData<Result<List<ListStoryItem>>> = _mapsResponse

    fun getLocationUser(token: String) {
        val liveData= userRepository.getStoriesWithLocation((token))
        _mapsResponse.addSource(liveData) {
            result -> _mapsResponse.value = result
        }
    }

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }
}