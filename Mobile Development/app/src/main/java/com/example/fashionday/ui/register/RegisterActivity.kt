package com.example.fashionday.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.fashionday.R
import com.example.fashionday.data.Result
import com.example.fashionday.data.ViewModelFactory
import com.example.fashionday.databinding.ActivityRegisterBinding
import com.example.fashionday.ui.home.HomeViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val factory : ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel : RegisterViewModel by viewModels {
        factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {




            btnRegister.setOnClickListener {
                val username = edtUsername.text.toString().toRequestBody("text/plain".toMediaType())
                val email = edtEmail.text.toString().toRequestBody("text/plain".toMediaType())
                val password = edtPassword.text.toString().toRequestBody("text/plain".toMediaType())

                viewModel.postRegister(username, email, password).observe(this@RegisterActivity){ result->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.btnRegister.text = "Loading..."
                            }
                            is Result.Success -> {
                                val msg = result.data.messages
                                alertDialog(msg)
                            }

                            is Result.Error -> {
                                binding.btnRegister.text = "Sign up"
                                Toast.makeText(
                                    this@RegisterActivity,
                                    result.error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }

                }

            }
        }



    }

    private fun alertDialog(msg : String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Success")
        alertDialogBuilder.setMessage("$msg")

        alertDialogBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
            finish()
        }

        alertDialogBuilder.show()
    }
}