package submission.learning.storyapp.interfaces.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import submission.learning.storyapp.data.response.LoginUserResponse
import submission.learning.storyapp.helper.Result
import submission.learning.storyapp.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    private val _loginAction = MediatorLiveData<Result<LoginUserResponse>>()
    val loginActionModel: LiveData<Result<LoginUserResponse>> = _loginAction

    fun login(email: String, password: String) {
        val liveData = userRepository.login(email, password)
        _loginAction.addSource(liveData) {
            result -> _loginAction.value = result
        }
    }
}