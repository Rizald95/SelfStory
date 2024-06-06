package submission.learning.storyapp.interfaces.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import submission.learning.storyapp.data.response.RegisterUserResponse
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.helper.Result
class RegisterViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _registerUserResponse = MediatorLiveData<Result<RegisterUserResponse>>()

    val regiserUserResponse: LiveData<Result<RegisterUserResponse>> = _registerUserResponse

    fun registerUser(name: String, email: String, password: String) {
        val liveData = userRepository.register(name, email, password)
        _registerUserResponse.addSource(liveData){
            result -> _registerUserResponse.value = result
        }
    }

}