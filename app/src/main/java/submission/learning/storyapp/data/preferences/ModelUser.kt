package submission.learning.storyapp.data.preferences

data class ModelUser (
    val email: String,
    val token: String,
    val isLogin: Boolean = false
    )