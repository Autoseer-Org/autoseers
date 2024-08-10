package com.innovara.autoseers.api.di

import com.innovara.autoseers.api.home.AlertsService
import com.innovara.autoseers.api.home.AlertsServiceImpl
import com.innovara.autoseers.api.home.HomeService
import com.innovara.autoseers.api.home.HomeServiceImpl
import com.innovara.autoseers.api.onboarding.OnboardingService
import com.innovara.autoseers.api.onboarding.OnboardingServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object ApiModule {
    @Provides
    fun provideOnboardingService(
        retrofit: Retrofit
    ): OnboardingService =
        OnboardingServiceImpl(retrofit)

    @Provides
    fun provideHomeService(
        retrofit: Retrofit,
    ): HomeService = HomeServiceImpl(retrofit)

    @Provides
    fun provideAlertService(
        retrofit: Retrofit,
    ): AlertsService = AlertsServiceImpl(retrofit)
}