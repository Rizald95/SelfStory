package submission.learning.storyapp.interfaces.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityMainBinding
import submission.learning.storyapp.helper.ViewModelFactory
import submission.learning.storyapp.interfaces.adapter.ListUserAdapter
import submission.learning.storyapp.interfaces.story.InsertStoryActivity
import submission.learning.storyapp.interfaces.welcome.WelcomeActivity
import submission.learning.storyapp.helper.Result
import submission.learning.storyapp.interfaces.adapter.ListUserLocationAdapter
import submission.learning.storyapp.interfaces.adapter.LoadingStateAdapter
import submission.learning.storyapp.interfaces.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var storyAdapter: ListUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryList.layoutManager = layoutManager

        setupViewActivity()

        viewModel.getSession().observe(this) {
            user ->
            val token = user.token
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setNewData(token)
            }
        }



//        viewModel.listStoryItem.observe(this) {
//            when (it) {
//                is Result.Loading -> {
//                    showLoading(true)
//                }
//
//                is Result.Error -> {
//                    showLoading(false)
//                }
//
//                is Result.Success -> {
//                    showLoading(false)
//                    storyAdapter = ListUserAdapter(it.data)
//                    binding.rvStoryList.adapter = storyAdapter
//                }
//
//            }
//        }

        binding.addStory.setOnClickListener {
            val intent = Intent(this, InsertStoryActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setNewData(token: String) {
        val listUserLocationAdapter = ListUserLocationAdapter()
        binding.rvStoryList.adapter = listUserLocationAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listUserLocationAdapter.retry()
            }
        )
        viewModel.listStoryLocation(token).observe(this) {
            listUserLocationAdapter.submitData(lifecycle, it)
        }
        showLoading(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflateMenu: MenuInflater = menuInflater
        inflateMenu.inflate(R.menu.menu_story, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
           R.id.btn_logout -> {
               viewModel.logOut()
           }

           R.id.btn_maps -> {
               val maps = Intent(this, MapsActivity::class.java)
               startActivity(maps)
           }
       }

        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun setupViewActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.show()
    }


    private fun showLoading(isLoading: Boolean) {
        binding.rvStoryList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}