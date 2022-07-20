package com.androidstudiorheinald.mystoryapp2intermediate.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.androidstudiorheinald.mystoryapp2intermediate.R
import com.androidstudiorheinald.mystoryapp2intermediate.databinding.ActivityLoginBinding
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.LoginViewModel
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authenticationModel: AuthenticationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val pref = AuthenticationPreferences.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.getAuthentication().observe(this) { auth ->
            authenticationModel = auth

            if(authenticationModel.isLogin) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_STORY, auth.token)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailSignIn.text.toString()
            val password = binding.etPasswordSignIn.text.toString()
            when {
                email.isEmpty() -> {
                    binding.etEmailLayoutSignIn.error = getString(R.string.enter_email)
                }
                email.isNotEmpty() && ! Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmailLayoutSignIn.error = getString(R.string.email_invalid)
                }
                else -> {
                    loginViewModel.setLogin(email, password)

                    loginViewModel.login.observe(this) { loginResult ->
                        if(loginResult != null) {
                            session(AuthenticationModel(loginResult.token, true))
                        }
                    }
                }
            }
        }

        binding.btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun session(authenticationModel: AuthenticationModel) {
        loginViewModel.saveAuthentication(authenticationModel)
    }
}