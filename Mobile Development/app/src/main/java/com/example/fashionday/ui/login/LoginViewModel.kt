package com.example.fashionday.ui.login

import androidx.lifecycle.ViewModel
import com.example.fashionday.data.FashionRepository
import okhttp3.RequestBody

class LoginViewModel(private val fashionRepository: FashionRepository) : ViewModel() {
    fun postLogin(username: String, password: String) =
        fashionRepository.postLogin(username, password)
}