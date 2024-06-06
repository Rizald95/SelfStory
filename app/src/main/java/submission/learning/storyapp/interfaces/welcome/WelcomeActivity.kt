package submission.learning.storyapp.interfaces.welcome

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityWelcomeBinding
import submission.learning.storyapp.interfaces.login.LoginActivity
import submission.learning.storyapp.interfaces.register.RegisterActivity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View


class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        binding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        playNewAnimation()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun playNewAnimation() {
        // Animasi untuk gambar, berputar-putar
        val imageRotation = ObjectAnimator.ofFloat(binding.imageView, View.ROTATION, 0f, 360f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }

        // Animasi untuk tombol login dan signup, fade in dan bergerak ke atas
        val loginTranslationY = ObjectAnimator.ofFloat(binding.loginButton, View.TRANSLATION_Y, 50f, 0f).setDuration(500)
        val loginAlpha = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).setDuration(500)

        val signupTranslationY = ObjectAnimator.ofFloat(binding.signupButton, View.TRANSLATION_Y, 50f, 0f).setDuration(500)
        val signupAlpha = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).setDuration(500)

        // Animasi untuk teks title dan desc, fade in dan bergerak ke bawah
        val titleTranslationY = ObjectAnimator.ofFloat(binding.titleTextView, View.TRANSLATION_Y, -30f, 0f).setDuration(500)
        val titleAlpha = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).setDuration(500)

        val descTranslationY = ObjectAnimator.ofFloat(binding.descTextView, View.TRANSLATION_Y, -30f, 0f).setDuration(500)
        val descAlpha = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 0f, 1f).setDuration(500)

        // Set animasi bersama-sama
        val set = AnimatorSet()
        set.playTogether(
            imageRotation,
            loginTranslationY, loginAlpha,
            signupTranslationY, signupAlpha,
            titleTranslationY, titleAlpha,
            descTranslationY, descAlpha
        )
        set.start()
    }
}