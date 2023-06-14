package com.example.fashionday.data.response

import com.google.gson.annotations.SerializedName

data class DataItem(
    @field:SerializedName("photo")
    val photo: String
)