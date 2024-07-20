package com.innovara.autoseers.di.retrofit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object RetrofitModule {
    const val BASE_URL = "https://autoseers-woegjoq56q-ue.a.run.app/"

    @Provides
    fun provideRetrofit(): Retrofit {
        val mediaType = "application/json".toMediaType()
        return Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(mediaType))
            .build()
    }
}