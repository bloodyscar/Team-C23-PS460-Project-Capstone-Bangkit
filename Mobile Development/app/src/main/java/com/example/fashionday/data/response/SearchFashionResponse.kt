package com.example.fashionday.data.response

import com.google.gson.annotations.SerializedName

data class SearchFashionResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("predictions")
	val predictions: String,

	@field:SerializedName("status")
	val status: String
)


