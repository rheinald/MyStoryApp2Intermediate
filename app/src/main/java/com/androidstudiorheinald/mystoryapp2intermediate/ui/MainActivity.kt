package com.androidstudiorheinald.mystoryapp2intermediate.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidstudiorheinald.mystoryapp2intermediate.R
import com.androidstudiorheinald.mystoryapp2intermediate.StoryRepository
import com.androidstudiorheinald.mystoryapp2intermediate.adapter.ListStoryAdapter
import com.androidstudiorheinald.mystoryapp2intermediate.adapter.LoadingStateAdapter
import com.androidstudiorheinald.mystoryapp2intermediate.api.ApiConfig
import com.androidstudiorheinald.mystoryapp2intermediate.databinding.ActivityMainBinding
import com.androidstudiorheinald.mystoryapp2intermediate.model.AuthenticationModel
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryItem
import com.androidstudiorheinald.mystoryapp2intermediate.util.AuthenticationPreferences
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.MainViewModel
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.PagingReposViewModel
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.PagingViewModelFactory
import com.androidstudiorheinald.mystoryapp2intermediate.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "list_story_activity")
    private lateinit var binding: ActivityMainBinding
    private lateinit var listStoryAdapter: ListStoryAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pagingReposViewModel: PagingReposViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthenticationPreferences.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        val storyRepos = StoryRepository(apiService, pref)

        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
        pagingReposViewModel = ViewModelProvider(this, PagingViewModelFactory(storyRepos))[PagingReposViewModel::class.java]

        listStoryAdapter = ListStoryAdapter()

        binding.rvStory.apply {
            adapter = listStoryAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    listStoryAdapter.retry()
                }
            )
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        mainViewModel.getAuthentication().observe(this) { auth ->
            if(auth.isLogin) {
                pagingReposViewModel.story.observe(this) {
                    listStoryAdapter.submitData(lifecycle, it)
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        pagingReposViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.util_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                login(false)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.to_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.to_add_story -> {
                mainViewModel.getAuthentication().observe(this) { auth ->
                    val intent = Intent(this, AddStoryActivity::class.java)
                    intent.putExtra(AddStoryActivity.EXTRA_TOKEN, auth.token)
                    startActivity(intent)
                }
            }
        }
        return true
    }

    private fun login(isLogin: Boolean) {
        mainViewModel.saveAuthentication(AuthenticationModel("", isLogin))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarStory.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}