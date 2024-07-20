package com.innovara.autoseers.di.analytics.onboardingevents

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
/*
 * Dagger Hilt doesn't allow dependencies from shorter-lived components (ActivityScope)
 * to be injected into longer-lived components (SingletonScope).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingAnalyticsEventsModule {
    @Binds
    @Singleton
    abstract fun bindOnboardingAnalyticsEvents(onboardingAnalyticsEventsImpl: OnboardingAnalyticsEventsImpl): OnboardingAnalyticsEvents
}