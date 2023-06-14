package com.example.fashionday.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionday.R
import com.example.fashionday.data.Result
import com.example.fashionday.data.ViewModelFactory
import com.example.fashionday.data.response.DataItem
import com.example.fashionday.databinding.ActivityHomeBinding
import com.example.fashionday.ui.BestTodayAdapter
import com.example.fashionday.ui.upload.UploadFileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel :  HomeViewModel by viewModels {
        factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeLottieAnimationView.visibility = View.GONE

        binding.rvBest.setHasFixedSize(true)

        binding.btnCamera.setOnClickListener{
            val intent = Intent(this, UploadFileActivity::class.java)
            startActivity(intent)
        }

        showBestToday()

    }



    private fun showBestToday() {
        viewModel.getListBestToday().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.homeLottieAnimationView.visibility = View.VISIBLE
                        Log.d("L0Ading", "Loading....")
                    }
                    is Result.Success -> {
                        binding.homeLottieAnimationView.visibility = View.GONE
                        val data = result.data
                        showRecyclerList(data)
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            this@HomeActivity,
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    private fun showRecyclerList(list : List<DataItem>) {
        binding.rvBest.layoutManager = GridLayoutManager(this, 2)
        val listBest = BestTodayAdapter(list)
        binding.rvBest.adapter = listBest
    }


}