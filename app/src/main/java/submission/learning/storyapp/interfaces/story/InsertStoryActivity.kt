package submission.learning.storyapp.interfaces.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import submission.learning.storyapp.R
import submission.learning.storyapp.helper.ViewModelFactory
import submission.learning.storyapp.helper.Result
import submission.learning.storyapp.databinding.ActivityInsertStoryBinding
import submission.learning.storyapp.helper.getImageUri
import submission.learning.storyapp.helper.reduceFileImage
import submission.learning.storyapp.helper.uriToFile
import submission.learning.storyapp.interfaces.main.MainActivity

class InsertStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertStoryBinding
    private val viewModel by viewModels<InsertViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            requestPermission.launch(REQUIRED_PERMISSION)
        }

        val launcherIntentCamera = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                showImage()
            }
        }

        postStory()

        binding.buttonCamera.setOnClickListener {
            val uri = getImageUri(this)
            currentImageUri = uri
            uri?.let {
                launcherIntentCamera.launch(it)
            } ?: showToast(getString(R.string.empty_image))
        }

        binding.buttonGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonUpload.setOnClickListener {
            var token: String
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val description = binding.editTextDescription.text.toString()

                if (description.isEmpty()) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Please tell us about your story :)")
                        setMessage(getString(R.string.empty_description))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.ok_message)) { _, _ ->
                            val intent = Intent(context, InsertStoryActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                } else {
                    showLoading(true)

                    val requestBody = description.toRequestBody("text/plain".toMediaType())
                    val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "photo",
                        imageFile.name,
                        requestImageFile
                    )

                    viewModel.getSession().observe(this) { user ->
                        token = user.token
                        viewModel.addStory(token, multipartBody, requestBody)
                    }
                }
            } ?: showToast(getString(R.string.empty_image))
        }
    }

    private fun postStory() {
        viewModel.insertStoryResponse.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                    disableInterface()
                }
                is Result.Success -> {
                    showLoading(false)
                    enableInterface()
                    AlertDialog.Builder(this).apply {
                        setTitle("Alright")
                        setMessage(getString(R.string.upload_message))
                        setCancelable(false)
                        setPositiveButton(getString(R.string.next)) { _, _ ->
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
                    showLoading(false)
                    enableInterface()
                }
            }
        }
    }

    private fun disableInterface() {
        binding.buttonCamera.isEnabled = false
        binding.buttonGallery.isEnabled = false
        binding.buttonUpload.isEnabled = false
        binding.editTextDescription.isEnabled = false
    }

    private fun enableInterface() {
        binding.buttonCamera.isEnabled = true
        binding.buttonGallery.isEnabled = true
        binding.buttonUpload.isEnabled = true
        binding.editTextDescription.isEnabled = true
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        when (isGranted) {
            true -> Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            false -> Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "show Image:$it")
            binding.imageViewPreview.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
