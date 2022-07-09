package com.androidstudiorheinald.mystoryapp2intermediate.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story")
data class ListStoryResponse(

    @field:SerializedName("listStory")
    val listStory: ArrayList<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)