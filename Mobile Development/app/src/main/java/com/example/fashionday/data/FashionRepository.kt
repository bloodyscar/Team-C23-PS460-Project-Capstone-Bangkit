package com.example.fashionday.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.fashionday.data.response.BestTodayResponse
import com.example.fashionday.data.response.DataItem
import com.example.fashionday.data.response.LoginResponse
import com.example.fashionday.data.response.RegisterResponse
import com.example.fashionday.data.response.ResultPredictionResponse
import com.example.fashionday.data.response.SearchFashionResponse
import com.example.fashionday.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FashionRepository(private val apiService: ApiService) {

    fun getBestToday(): LiveData<Result<List<DataItem>>> {
        val listBest = MediatorLiveData<Result<List<DataItem>>>()

        listBest.postValue(Result.Loading)

        var client = apiService.getBestToday()
        client.enqueue(object : Callback<BestTodayResponse> {
            override fun onResponse(
                call: Call<BestTodayResponse>,
                response: Response<BestTodayResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ADABWANG", responseBody.toString())
                    if (responseBody != null) {
                        listBest.value = Result.Success(responseBody.data)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("response")
                    } catch (e: JSONException) {
                        "Unknown error"
                    }
                    listBest.value = Result.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<BestTodayResponse>, t: Throwable) {
                Log.e("A2DABWANG", "onFailure: ${t.message}")
            }
        })

        return listBest
    }

    fun postSearchImage(
        gambar: MultipartBody.Part,
        gender: RequestBody,
    ): LiveData<Result<ResultPredictionResponse>> {
        val listSearch = MediatorLiveData<Result<ResultPredictionResponse>>()

        listSearch.postValue(Result.Loading)

        var client = apiService.postSearch(gambar, gender)

        client.enqueue(object : Callback<ResultPredictionResponse> {
            override fun onResponse(
                call: Call<ResultPredictionResponse>,
                response: Response<ResultPredictionResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ADABWANG", responseBody.toString())
                    if (responseBody != null) {
                        listSearch.value = Result.Success(responseBody)
                    }

                } else {
                    Log.d("A2DABWANG", "ERRRPR BWANG")
                }
            }

            override fun onFailure(call: Call<ResultPredictionResponse>, t: Throwable) {
                Log.d("A2DABWANG", t.message.toString())
            }

        })

        return listSearch
    }

    fun postRegister(
        username: RequestBody,
        email: RequestBody,
        password: RequestBody,
    ): LiveData<Result<RegisterResponse>> {
        val resultRegister = MediatorLiveData<Result<RegisterResponse>>()

        resultRegister.postValue(Result.Loading)

        var client = apiService.postRegister(username, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ADABWANG", responseBody.toString())
                    if (responseBody != null) {
                        resultRegister.value = Result.Success(responseBody)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("response")
                    } catch (e: JSONException) {
                        "Unknown error"
                    }
                    resultRegister.value = Result.Error(errorMessage)

                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("A2DABWANG", "ERRRPR BWANG")
            }

        })

        return resultRegister
    }

    fun postLogin(
        username: String,
        password: String,
    ): LiveData<Result<LoginResponse>> {
        val resultLogin = MediatorLiveData<Result<LoginResponse>>()

        resultLogin.postValue(Result.Loading)

        var client = apiService.postLogin(username, password)

        client.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ADABWANG", responseBody.toString())
                    if (responseBody != null) {
                        resultLogin.value = Result.Success(responseBody)
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorBody).getString("response")
                    } catch (e: JSONException) {
                        "Unknown error"
                    }
                    resultLogin.value = Result.Error(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("A2DABWANG", t.message.toString())

            }

        })

        return resultLogin
    }

    companion object {
        @Volatile
        private var instance: FashionRepository? = null
        fun getInstance(
            apiService: ApiService
        ): FashionRepository =
            instance ?: synchronized(this) {
                instance ?: FashionRepository(apiService)
            }.also { instance = it }
    }

}