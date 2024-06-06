package submission.learning.storyapp.interfaces.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityRegisterBinding
import submission.learning.storyapp.helper.ViewModelFactory
import submission.learning.storyapp.interfaces.custom.EditTextCustom
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import submission.learning.storyapp.helper.Result
import submission.learning.storyapp.interfaces.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var editTextCustom: EditTextCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editTextCustom = binding.passwordEditText
        setupView()
        playNewAnimation()
        setupAction()


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

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            binding.apply {
                if (nameEditText.error.isNullOrEmpty() && emailEditText.error.isNullOrEmpty() && passwordEditText.error.isNullOrEmpty()) {
                    val name = nameEditText.text.toString().trim()
                    val email = emailEditText.text.toString().trim()
                    val password = passwordEditText.text.toString().trim()
                    viewModel.registerUser(name, email, password)
                } else {
                    toastFailed()
                }
            }
        }

       viewModel.regiserUserResponse.observe(this) {
           when (it) {
               is Result.Loading -> {
                   showLoading(true)
               }
               is Result.Success -> {
                   showLoading(false)
                   AlertDialog.Builder(this).apply {
                       setTitle("Alright!")
                       setMessage("Account with ${binding.emailEditText.text} is created. Let's Login!")
                       setCancelable(false)
                       setPositiveButton("Login") { _, _ ->
                           val intent = Intent(context, LoginActivity::class.java)
                           startActivity(intent)
                           finish()
                       }
                       create()
                       show()
                   }
               }
               is Result.Error -> {
                   toastFailed()
                   showLoading(false)
               }
           }
       }

    }

    private fun toastFailed() {
        Toast.makeText(
            this,
            R.string.failed_register,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playNewAnimation() {
        // Animasi untuk gambar, fade in dan move up
        val imageFadeIn = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 0f, 1f).apply {
            duration = 1000
        }
        val imageMoveUp = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, 50f, 0f).apply {
            duration = 1000
        }

        // Animasi untuk masing-masing komponen
        val titleFadeIn = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val nameFadeIn = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val nameEditFadeIn = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val emailFadeIn = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val emailEditFadeIn = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordFadeIn = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordEditFadeIn = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 0f, 1f).setDuration(500)
        val signupFadeIn = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 0f, 1f).setDuration(500)

        // Set animasi bersama-sama
        val set = AnimatorSet()
        set.playTogether(
            imageFadeIn, imageMoveUp,
            titleFadeIn, nameFadeIn, nameEditFadeIn,
            emailFadeIn, emailEditFadeIn,
            passwordFadeIn, passwordEditFadeIn,
            signupFadeIn
        )
        set.startDelay = 200
        set.start()
    }
}