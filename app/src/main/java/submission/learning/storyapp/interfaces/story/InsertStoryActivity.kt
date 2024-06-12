package submission.learning.storyapp.interfaces.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import submission.learning.storyapp.R
import submission.learning.storyapp.databinding.ActivityInsertStoryBinding
import submission.learning.storyapp.helper.*
import submission.learning.storyapp.interfaces.main.MainActivity
import kotlinx.coroutines.launch
import android.provider.Settings

class InsertStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertStoryBinding
    private val viewModel by viewModels<InsertViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }
            else -> binding.switchLocation.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissions()
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
                        setTitle("Ceritakan cerita kamu:)")
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
                        viewModel.addStoryWithLocation(token, multipartBody, requestBody, currentLocation)
                    }
                }
            } ?: showToast(getString(R.string.empty_image))
        }

        binding.switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (!isGpsEnabled()) {
                    showGpsMessage()
                }
                lifecycleScope.launch {
                    getLastLocation()
                }
            } else {
                currentLocation = null
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @Suppress("MissingPermission")
    private fun getLastLocation() {
        if (allPermissionsGranted()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this,
                        R.string.location_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    requestNewLocation()
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGpsMessage() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.gps_message_title))
            setMessage(getString(R.string.gps_message))
            setPositiveButton(getString(R.string.next)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            create()
            show()
        }
    }

    @Suppress("MissingPermission")
    private fun requestNewLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
            numUpdates = 1
        }

        if (allPermissionsGranted()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            currentLocation = locationResult.lastLocation
        }
    }

    private fun postStory() {
        viewModel.insertStoryResponse.observe(this) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                    setInterfaceEnabled(false)
                }
                is Result.Success -> {
                    showLoading(false)
                    setInterfaceEnabled(true)
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
                    setInterfaceEnabled(true)
                }
            }
        }
    }

    private fun setInterfaceEnabled(isEnabled: Boolean) {
        binding.apply {
            buttonCamera.isEnabled = isEnabled
            buttonGallery.isEnabled = isEnabled
            buttonUpload.isEnabled = isEnabled
            editTextDescription.isEnabled = isEnabled
        }
    }

    private fun disableInterface() {
        setInterfaceEnabled(false)
    }

    private fun enableInterface() {
        setInterfaceEnabled(true)
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
