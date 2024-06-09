package submission.learning.storyapp.interfaces.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityLoginBinding
import submission.learning.storyapp.helper.ViewModelFactory
import submission.learning.storyapp.helper.Result
import androidx.appcompat.app.AlertDialog
import submission.learning.storyapp.interfaces.main.MainActivity


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewLogin()
        setupActionLogin()
        playAnimationLogin()
        viewModel.loginActionModel.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Berhasil!")
                        setMessage(R.string.message_login)
                        setPositiveButton("Ke Halaman Utama") { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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



    private fun setupViewLogin() {
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

    private fun setupActionLogin() {
        binding.loginButton.setOnClickListener {
            binding.apply {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Email dan password harus diisi!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun playAnimationLogin() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleAnimator = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val messageAnimator = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTextViewAnimator = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayoutAnimator = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextViewAnimator = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayoutAnimator = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val loginButtonAnimator = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(600)

        AnimatorSet().apply {
            playSequentially(
                titleAnimator,
                messageAnimator,
                emailTextViewAnimator,
                emailEditTextLayoutAnimator,
                passwordTextViewAnimator,
                passwordEditTextLayoutAnimator,
                loginButtonAnimator
            )
            startDelay = 100
        }.start()
    }

    private fun toastFailed() {
        Toast.makeText(
            this,
            R.string.failed_login,
            Toast.LENGTH_SHORT
        ).show()
    }






    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}