package com.innovara.autoseers.di.firebase

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Inject

interface FirebaseService {
    fun auth(): FirebaseAuth
    fun analytics(): FirebaseAnalytics
}

class FirebaseServiceImpl @Inject constructor() : FirebaseService {
    override fun auth(): FirebaseAuth {
        return Firebase.auth
    }

    override fun analytics(): FirebaseAnalytics {
        return Firebase.analytics
    }
}