package submission.learning.storyapp.interfaces.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import submission.learning.storyapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}