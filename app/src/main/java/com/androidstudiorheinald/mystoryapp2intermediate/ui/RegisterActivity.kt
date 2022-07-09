package com.androidstudiorheinald.mystoryapp2intermediate.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.androidstudiorheinald.mystoryapp2intermediate.R
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.databinding.ActivityRegisterBinding
import com.androidstudiorheinald.mystoryapp2intermediate.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etNameSignUp.text.toString()
            val email = binding.etEmailSignUp.text.toString()
            val password = binding.etPasswordSignUp.text.toString()
            when {
                name.isEmpty() -> {
                    binding.etNameLayoutSignUp.error = getString(R.string.empty_name)
                }
                email.isNotEmpty() && ! Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmailLayoutSignUp.error = getString(R.string.empty_email)
                }
                email.isEmpty() -> {
                    binding.etEmailLayoutSignUp.error = getString(R.string.empty_email)
                }
                else -> {
                    val client = ApiConfig.getApiService().postRegister(name, email, password)
                    client.enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            if(response.isSuccessful) {
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegisterActivity, resources.getString(R.string.success_registration), Toast.LENGTH_SHORT).show()
                            }
                            if(!response.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, resources.getString(R.string.failed_registration), Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(this@RegisterActivity, resources.getString(R.string.failed_registration), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }
}