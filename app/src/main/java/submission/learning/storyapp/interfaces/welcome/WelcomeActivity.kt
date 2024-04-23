package submission.learning.storyapp.interfaces.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import submission.learning.storyapp.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
    }
}