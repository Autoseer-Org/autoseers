package com.innovara.autoseers.di.firebase

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject

interface FirebaseService {
    val auth: FirebaseAuth
    fun analytics(): FirebaseAnalytics
    fun remoteConfig(): FirebaseRemoteConfig
}

class FirebaseServiceImpl @Inject constructor() : FirebaseService {
    override val auth: FirebaseAuth = Firebase.auth
    override fun analytics(): FirebaseAnalytics = Firebase.analytics
    override fun remoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        val configuration = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5000
        }.toBuilder()
        remoteConfig.setConfigSettingsAsync(configuration.build())
        return remoteConfig
    }
}