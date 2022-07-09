package com.androidstudiorheinald.mystoryapp2intermediate

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiService
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryItem
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences

class StoryRepository(
    private val apiService: ApiService,
    private val pref: AuthenticationPreferences) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingSource(apiService, pref)
            }
        ).liveData
    }
}