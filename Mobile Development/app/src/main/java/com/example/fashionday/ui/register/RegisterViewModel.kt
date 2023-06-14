package com.example.fashionday.ui.register

import androidx.lifecycle.ViewModel
import com.example.fashionday.data.FashionRepository
import okhttp3.RequestBody

class RegisterViewModel(private val fashionRepository: FashionRepository) : ViewModel() {
    fun postRegister(username: RequestBody, email: RequestBody, password: RequestBody) =
        fashionRepository.postRegister(username, email, password)
}