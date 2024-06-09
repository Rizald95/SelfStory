package submission.learning.storyapp.interfaces.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import submission.learning.storyapp.data.response.RegisterUserResponse
import submission.learning.storyapp.repository.UserRepository
import submission.learning.storyapp.helper.Result
class RegisterViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _register = MediatorLiveData<Result<RegisterUserResponse>>()
    val registerViewModel: LiveData<Result<RegisterUserResponse>> = _register

    fun register(name: String, email: String, password: String) {
        val liveData = userRepository.register(name, email, password)
        _register.addSource(liveData){ result ->
            _register.value = result
        }
    }

}