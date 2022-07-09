package com.androidstudiorheinald.mystoryapp2intermediate.viewmodel

import androidx.lifecycle.*
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: AuthenticationPreferences) : ViewModel() {

    fun getAuthentication(): LiveData<AuthenticationModel> {
        return pref.getAuthentication().asLiveData()
    }

    fun saveAuthentication(authenticationModel: AuthenticationModel) {
        viewModelScope.launch {
            pref.saveAuthentication(authenticationModel)
        }
    }
}