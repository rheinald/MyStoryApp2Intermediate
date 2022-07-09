package com.androidstudiorheinald.mystoryapp2intermediate

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiService
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryItem
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val apiService: ApiService,
    private val pref: AuthenticationPreferences
    ) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val token = pref.getAuthentication().first().token
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory("Bearer $token", position, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if(position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if(responseData.isEmpty()) null else position + 1
            )
        } catch(exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}