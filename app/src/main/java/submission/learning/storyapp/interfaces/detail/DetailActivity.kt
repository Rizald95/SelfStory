package submission.learning.storyapp.interfaces.detail

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import submission.learning.storyapp.R
import submission.learning.storyapp.data.response.ListStoryItem
import submission.learning.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var listStoryItem: ListStoryItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listStoryItem = getListStoryItemFromIntent() ?: run {
            showError()
            return
        }

        setupUI(listStoryItem)
        setupView()
    }

    private fun getListStoryItemFromIntent(): ListStoryItem? {
        return if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("listItemStory", ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("listItemStory")
        }
    }

    private fun setupUI(listStoryItem: ListStoryItem) {
        binding.idName.text = listStoryItem.name
        binding.idDescription.text = listStoryItem.description

        Glide
            .with(this)
            .load(listStoryItem.photoUrl)
            .fitCenter()
            .into(binding.imageDetail)
    }

    private fun showError() {
        binding.idName.text = getString(R.string.error_title)
        binding.idDescription.text = getString(R.string.error_description)
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
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

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
