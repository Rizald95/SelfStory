package submission.learning.storyapp.dependencies

import android.content.Context
import submission.learning.storyapp.data.preferences.ModelUserPreferences
import submission.learning.storyapp.data.preferences.dataStore
import submission.learning.storyapp.data.retrofit.ApiConfig
import submission.learning.storyapp.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val preference = ModelUserPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(preference, apiService)
    }
}