package submission.learning.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class ModelUserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun  saveSessionLogin(user: ModelUser) {
        dataStore.edit {preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true

        }
    }
    fun getSession(): Flow<ModelUser>{
        return dataStore.data.map {preferences -> ModelUser(
            email = preferences[EMAIL_KEY] ?: "",
            token = preferences[TOKEN_KEY] ?: "",
            isLogin = preferences[IS_LOGIN_KEY] ?: false

        )}
    }

    suspend fun logout() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    companion object {
        @Volatile
        private var INSTANCE: ModelUserPreferences? = null
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>) : ModelUserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = ModelUserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}