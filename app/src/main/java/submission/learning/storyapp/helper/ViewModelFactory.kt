package submission.learning.storyapp.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import submission.learning.storyapp.dependencies.Injection
import submission.learning.storyapp.interfaces.login.LoginViewModel
import submission.learning.storyapp.interfaces.main.MainViewModel
import submission.learning.storyapp.interfaces.register.RegisterViewModel
import submission.learning.storyapp.interfaces.story.InsertViewModel
import submission.learning.storyapp.repository.UserRepository

class ViewModelFactory(private val userRepository: UserRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(InsertViewModel::class.java) -> {
                InsertViewModel(userRepository) as T
            }
            else -> throw  IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}