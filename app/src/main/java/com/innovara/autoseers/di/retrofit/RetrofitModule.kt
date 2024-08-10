package com.innovara.autoseers.di.retrofit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val BASE_URL = "https://autoseers-woegjoq56q-ue.a.run.app/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val mediaType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
        }
        val logger = HttpLoggingInterceptor()
        logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .callTimeout(Duration.ofMillis(25_000))
            .connectTimeout(Duration.ofMillis(25_000))
            .readTimeout(Duration.ofMillis(25_000))
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            .build()
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(json.asConverterFactory(mediaType))
            .baseUrl(BASE_URL)
            .build()
    }
}