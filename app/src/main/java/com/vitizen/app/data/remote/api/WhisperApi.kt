package com.vitizen.app.data.remote.api

import com.vitizen.app.data.remote.dto.WhisperResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {
    @Multipart
    @POST("v1/audio/transcriptions")
    @Headers("Content-Type: multipart/form-data")
    suspend fun transcribe(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody,
        @Part("response_format") responseFormat: RequestBody? = null,
        @Part("prompt") prompt: RequestBody? = null,
        @Part("timestamp_granularities") timestampGranularities: RequestBody? = null
    ): WhisperResponse

    @Multipart
    @POST("v1/audio/translations")
    @Headers("Content-Type: multipart/form-data")
    suspend fun translate(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("response_format") responseFormat: RequestBody? = null,
        @Part("prompt") prompt: RequestBody? = null
    ): WhisperResponse
} 