package com.example.fashionday.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionday.R
import com.example.fashionday.data.Result
import com.example.fashionday.data.ViewModelFactory
import com.example.fashionday.ui.register.RegisterActivity
import com.example.fashionday.ui.register.RegisterViewModel
import com.example.fashionday.databinding.ActivityLoginBinding
import com.example.fashionday.ui.home.HomeActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: LoginViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.apply {

            btnLogin.setOnClickListener {
                viewModel.postLogin(edtUsernameLogin.text.toString(), edtPassLogin.text.toString())
                    .observe(this@LoginActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    binding.btnLogin.text = "Loading..."
                                }

                                is Result.Success -> {
                                    val intent =
                                        Intent(this@LoginActivity, HomeActivity::class.java)
                                    finish()
                                    startActivity(intent)
                                }

                                is Result.Error -> {
                                    binding.btnLogin.text = "Login"
                                    Toast.makeText(
                                        this@LoginActivity,
                                        result.error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }
            }
        }


        setupHyperlink()


    }

    private fun setupHyperlink() {
        val linkTextView = findViewById<TextView>(R.id.tvRegister)
        linkTextView.movementMethod = LinkMovementMethod.getInstance();
    }

    private fun alertDialog(msg: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Success")
        alertDialogBuilder.setMessage("$msg")

        alertDialogBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
            finish()
        }

        alertDialogBuilder.show()
    }
}