package com.androidstudiorheinald.mystoryapp2intermediate.viewmodel

import androidx.lifecycle.*
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryItem
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryResponse
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: AuthenticationPreferences): ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _listStory = MutableLiveData<ArrayList<ListStoryItem>>()
    val listStory: LiveData<ArrayList<ListStoryItem>> = _listStory

    fun getAuthentication(): LiveData<AuthenticationModel> {
        return pref.getAuthentication().asLiveData()
    }

    fun saveAuthentication(authenticationModel: AuthenticationModel) {
        viewModelScope.launch {
            pref.saveAuthentication(authenticationModel)
        }
    }

    fun getStory(token: String) {
        val client = ApiConfig.getApiService().getStoryWithLocation("Bearer $token", 1)
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(call: Call<ListStoryResponse>, response: Response<ListStoryResponse>) {
                if(response.isSuccessful) {
                    _listStory.value = response.body()?.listStory
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _message.value = t.message
            }
        })
    }
}