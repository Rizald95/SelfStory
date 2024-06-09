package submission.learning.storyapp.interfaces.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityMainBinding
import submission.learning.storyapp.helper.ViewModelFactory
import submission.learning.storyapp.interfaces.adapter.ListUserAdapter
import submission.learning.storyapp.interfaces.story.InsertStoryActivity
import submission.learning.storyapp.interfaces.welcome.WelcomeActivity
import submission.learning.storyapp.helper.Result

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

        viewModel.getSession().observe(this) {
            user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStories(user.token)
            }
        }

        viewModel.listStoryItem.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Error -> {
                    showLoading(false)
                }

                is Result.Success -> {
                    showLoading(false)
                    storyAdapter = ListUserAdapter(it.data)
                    binding.rvStoryList.adapter = storyAdapter
                }

            }
        }







        binding.addStory.setOnClickListener {
            val intent = Intent(this, InsertStoryActivity::class.java)
            startActivity(intent)
        }


    }

    private fun showLoading(isLoading: Boolean) {
        binding.rvStoryList.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflateMenu: MenuInflater = menuInflater
        inflateMenu.inflate(R.menu.menu_story, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            viewModel.logOut()
        }

        return super.onOptionsItemSelected(item)
    }


}