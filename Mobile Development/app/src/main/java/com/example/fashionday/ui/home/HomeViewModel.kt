package com.example.fashionday.ui.home

import androidx.lifecycle.ViewModel
import com.example.fashionday.data.FashionRepository

class HomeViewModel(private val fashionRepository: FashionRepository) : ViewModel() {
    fun getListBestToday() = fashionRepository.getBestToday()
}