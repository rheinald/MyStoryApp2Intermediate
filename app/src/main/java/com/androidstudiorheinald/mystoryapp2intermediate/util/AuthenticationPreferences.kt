package com.androidstudiorheinald.mystoryapp2intermediate.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthenticationPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getAuthentication(): Flow<AuthenticationModel> {
        return dataStore.data.map { preferences ->
            AuthenticationModel(
                preferences[TOKEN_KEY] ?: "",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveAuthentication(authenticationModel: AuthenticationModel) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = authenticationModel.token
            preferences[STATE_KEY] = authenticationModel.isLogin
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthenticationPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): AuthenticationPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthenticationPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}