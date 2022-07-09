package com.androidstudiorheinald.mystoryapp2intermediate.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.androidstudiorheinald.mystoryapp2intermediate.R
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.databinding.ActivityAddStoryBinding
import com.androidstudiorheinald.mystoryapp2intermediate.model.FileUploadResponse
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import com.androidstudiorheinald.mystoryapp2intermediate.util.createTempFile
import com.androidstudiorheinald.mystoryapp2intermediate.util.reduceFileImage
import com.androidstudiorheinald.mystoryapp2intermediate.util.uriToFile
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.AddStoryViewModel
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "add_story")
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var token: String
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        token = intent.getStringExtra(EXTRA_TOKEN).toString()
        intent.putExtra(EXTRA_TOKEN, token)

        setupViewModel()

        if(! allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.apply {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadImage() }
        }
    }

    private fun setupViewModel() {
        val pref = AuthenticationPreferences.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AddStoryViewModel::class.java]

        addStoryViewModel.getAuthentication().observe(this) { auth ->
            if(auth.isLogin) {
                Toast.makeText(this, getString(R.string.share_your_story), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if(! allPermissionsGranted()) {
                Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.androidstudiorheinald.mystoryapp2intermediate",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if(getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            val service = ApiConfig.getApiService().uploadStory("Bearer $token", imageMultipart, description)
            service.enqueue(object : Callback<FileUploadResponse> {
                override fun onResponse(call: Call<FileUploadResponse>, response: Response<FileUploadResponse>) {
                    if(response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null && !responseBody.error) {
                            Toast.makeText(this@AddStoryActivity, getString(R.string.story_uploaded), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@AddStoryActivity, getString(R.string.upload_story_failed), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    Toast.makeText(this@AddStoryActivity, getString(R.string.failed_retrofit), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.feature_not_ready), Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                getFile = myFile
                val result = BitmapFactory.decodeFile(myFile.path)
                binding.imgAddStory.setImageBitmap(result)
            }
        }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this@AddStoryActivity)
                getFile = myFile
                binding.imgAddStory.setImageURI(selectedImg)
            }
        }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}