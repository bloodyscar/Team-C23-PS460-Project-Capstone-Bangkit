package com.example.fashionday.ui.upload

import androidx.lifecycle.ViewModel
import com.example.fashionday.data.FashionRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val fashionRepository: FashionRepository) : ViewModel() {
    fun postSearchImage(gambar: MultipartBody.Part, gender: RequestBody) =
        fashionRepository.postSearchImage(gambar, gender)
}