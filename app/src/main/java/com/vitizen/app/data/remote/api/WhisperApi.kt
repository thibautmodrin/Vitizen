package com.vitizen.app.data.remote.api

import com.vitizen.app.data.remote.dto.WhisperResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribe(
        @Part file: MultipartBody.Part,
        @Part model: MultipartBody.Part,
        @Part language: MultipartBody.Part,
        @Part responseFormat: MultipartBody.Part,
        @Part prompt: MultipartBody.Part? = null
    ): WhisperResponse

    @Multipart
    @POST("v1/audio/translations")
    suspend fun translate(
        @Part file: MultipartBody.Part,
        @Part model: MultipartBody.Part,
        @Part responseFormat: MultipartBody.Part,
        @Part prompt: MultipartBody.Part? = null
    ): WhisperResponse
} 