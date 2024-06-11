package submission.learning.storyapp.interfaces.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import submission.learning.storyapp.data.preferences.ModelUser
import submission.learning.storyapp.data.response.ListStoryItem
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.helper.Result

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _listStoryItem = MediatorLiveData<Result<List<ListStoryItem>>>()
    val listStoryItem: LiveData<Result<List<ListStoryItem>>> = _listStoryItem

    fun getStories(token: String) {
        val liveData = userRepository.getStories(token)
        _listStoryItem.addSource(liveData) {
            result -> _listStoryItem.value = result
        }
    }

    fun getSession(): LiveData<ModelUser> {
        return userRepository.getSession().asLiveData()
    }

    fun logOut() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun listStoryLocation(token: String): LiveData<PagingData<ListStoryItem>> =
        userRepository.getStoriesLocation(token).cachedIn(viewModelScope)


}