package com.example.fashionday.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("response")
	val response: Response,

	@field:SerializedName("status")
	val status: Int
)

data class Response(

	@field:SerializedName("logged_in")
	val loggedIn: Boolean,

	@field:SerializedName("userid")
	val userid: Int,

	@field:SerializedName("username")
	val username: String
)
