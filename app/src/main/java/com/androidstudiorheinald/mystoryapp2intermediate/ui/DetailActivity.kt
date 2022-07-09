package com.androidstudiorheinald.mystoryapp2intermediate.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidstudiorheinald.mystoryapp2intermediate.R
import com.androidstudiorheinald.mystoryapp2intermediate.databinding.ActivityDetailBinding
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryItem
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupSharedElement()
    }

    private fun setupSharedElement() {
        val story = intent.getParcelableExtra<ListStoryItem>("Story") as ListStoryItem
        binding.apply {
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_baseline_photo_24px)
                .circleCrop()
                .into(imgStoryDetail)
            tvNameStoryDetail.text = story.name
            tvDescriptionStoryDetail.text = story.description
        }
    }
}