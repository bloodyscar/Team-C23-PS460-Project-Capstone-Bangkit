package com.example.fashionday.di

import android.content.Context
import com.example.fashionday.data.FashionRepository
import com.example.fashionday.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): FashionRepository {
        val apiService = ApiConfig.getApiService()
        return FashionRepository.getInstance(apiService)
    }
}