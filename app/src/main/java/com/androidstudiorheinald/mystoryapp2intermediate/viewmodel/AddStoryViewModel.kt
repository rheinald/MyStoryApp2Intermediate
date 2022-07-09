package com.androidstudiorheinald.mystoryapp2intermediate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.model.FileUploadResponse
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel(private val pref: AuthenticationPreferences): ViewModel() {

    fun getAuthentication(): LiveData<AuthenticationModel> {
        return pref.getAuthentication().asLiveData()
    }
}