package com.example.fashionday.ui.upload

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionday.R
import com.example.fashionday.createCustomTempFile
import com.example.fashionday.data.Result
import com.example.fashionday.data.ViewModelFactory
import com.example.fashionday.data.response.DataItem
import com.example.fashionday.databinding.ActivityUploadFileBinding
import com.example.fashionday.exif
import com.example.fashionday.reduceFileImage
import com.example.fashionday.uriToFile
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadFileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityUploadFileBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: UploadViewModel by viewModels {
        factory
    }
    private var getFile: File? = null
    private lateinit var itemSelected : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvRPred.setHasFixedSize(true)

        val spinGender = binding.spinGender
        binding.backBtn.setOnClickListener {
            finish()
        }
        spinGender.onItemSelectedListener = this
        binding.lottieAnimationView.visibility = View.GONE

        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = "Upload Image"
        actionBar?.setDisplayHomeAsUpEnabled(true)


//      hide rv result prediction
        binding.rvRPred.visibility = View.GONE
        binding.tvPred.visibility = View.GONE
        binding.tvRPrediction.visibility = View.GONE


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinGender.adapter = adapter
        }



        binding.ivEmpty.setOnClickListener {
            showBottomSheetDialog()
        }
        binding.btnUpload.setOnClickListener {
            uploadPhoto(viewModel)
        }
    }

    private fun uploadPhoto(
        viewModel: UploadViewModel
    ) {
        if (getFile != null) {
            lifecycleScope.launch {
                var file = reduceFileImage(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val gambar: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "gambar",
                    file.name,
                    requestImageFile
                )
                viewModel.postSearchImage(
                    gambar,
                    itemSelected.toRequestBody("text/plain".toMediaType()),
                ).observe(this@UploadFileActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.lottieAnimationView.visibility = View.VISIBLE
                                binding.lottieAnimationView.apply {
                                    playAnimation()
                                }
                            }

                            is Result.Success -> {
                                binding.lottieAnimationView.cancelAnimation()
                                binding.lottieAnimationView.visibility = View.GONE
                                val data = result.data
                                binding.tvRPrediction.text = data.predictions.toTitleCase()
                                Log.d("L0Ading", data.toString())
                                Log.d("Pr3dcs", data.predictions)
                                showRecyclerList(data.data)
                                //      hide rv result prediction
                                binding.rvRPred.visibility = View.VISIBLE
                                binding.tvPred.visibility = View.VISIBLE
                                binding.tvRPrediction.visibility = View.VISIBLE
                            }

                            is Result.Error -> {
                                binding.lottieAnimationView.cancelAnimation()
                                binding.lottieAnimationView.visibility = View.GONE
                                Toast.makeText(
                                    this@UploadFileActivity,
                                    "ERRORRRRR",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

                }
            }
        } else {
            Toast.makeText(
                this@UploadFileActivity,
                "Please upload image first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = exif(currentPhotoPath)

            binding?.apply {
                ivEmpty.setImageBitmap(result)
            }


        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadFileActivity,
                "com.example.fashionday",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    fun String.toTitleCase(): String {
        return split(" ").joinToString(" ") { it.capitalize() }
    }

    private fun showRecyclerList(list: List<DataItem>) {
        binding.rvRPred.layoutManager = GridLayoutManager(this, 2)
        val listBest = UploadAdapter(list)
        binding.rvRPred.adapter = listBest
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)
        val folder = bottomSheetDialog.findViewById<LinearLayout>(R.id.folderLinearLayout)
        val camera = bottomSheetDialog.findViewById<LinearLayout>(R.id.cameraLinearLayout)

        bottomSheetDialog.show()

        folder?.setOnClickListener {
            startGallery()
            bottomSheetDialog.dismiss();
        }

        camera?.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
            if (allPermissionsGranted()) {
                startTakePhoto()
            } else {
                Toast.makeText(this@UploadFileActivity, "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
            bottomSheetDialog.dismiss();
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadFileActivity)
                getFile = myFile
                binding.ivEmpty.setImageURI(uri)
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val result = parent?.getItemAtPosition(position).toString()
        itemSelected = result
        Toast.makeText(this, itemSelected, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}