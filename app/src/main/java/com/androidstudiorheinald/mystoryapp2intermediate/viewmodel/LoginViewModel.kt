package com.androidstudiorheinald.mystoryapp2intermediate.viewmodel

import androidx.lifecycle.*
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.model.LoginResponse
import com.androidstudiorheinald.mystoryapp2intermediate.model.LoginResult
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: AuthenticationPreferences) : ViewModel() {

    private val _login = MutableLiveData<LoginResult>()
    val login: LiveData<LoginResult> = _login

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getAuthentication(): LiveData<AuthenticationModel> {
        return pref.getAuthentication().asLiveData()
    }

    fun saveAuthentication(authenticationModel: AuthenticationModel) {
        viewModelScope.launch {
            pref.saveAuthentication(authenticationModel)
        }
    }

    fun setLogin(email: String, password: String) {
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful) {
                    _login.value = response.body()?.loginResult
                    _message.value = response.body()?.message
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _message.value = t.message
            }
        })
    }
}