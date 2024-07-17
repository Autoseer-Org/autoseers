package com.innovara.autoseers.di.firebase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class FirebaseModule {
    @Binds
    abstract fun bindFirebaseAuth(firebaseAuthServiceImpl: FirebaseAuthServiceImpl): FirebaseAuthService
}