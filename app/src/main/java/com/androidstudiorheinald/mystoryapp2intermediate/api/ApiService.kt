package com.androidstudiorheinald.mystoryapp2intermediate.api

import com.androidstudiorheinald.mystoryapp2intermediate.model.FileUploadResponse
import com.androidstudiorheinald.mystoryapp2intermediate.model.ListStoryResponse
import com.androidstudiorheinald.mystoryapp2intermediate.model.LoginResponse
import com.androidstudiorheinald.mystoryapp2intermediate.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ListStoryResponse

    @GET("stories")
    fun getStoryWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<ListStoryResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<FileUploadResponse>
}