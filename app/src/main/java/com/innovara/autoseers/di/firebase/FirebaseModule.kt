package com.innovara.autoseers.di.firebase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {
    @Binds
    abstract fun bindFirebaseAuth(firebaseAuthServiceImpl: FirebaseServiceImpl): FirebaseService
}