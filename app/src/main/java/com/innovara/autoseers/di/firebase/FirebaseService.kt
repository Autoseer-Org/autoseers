package com.innovara.autoseers.di.firebase

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Inject

interface FirebaseService {
    val auth: FirebaseAuth
    fun analytics(): FirebaseAnalytics
}

class FirebaseServiceImpl @Inject constructor() : FirebaseService {
    override val auth: FirebaseAuth = Firebase.auth
    override fun analytics(): FirebaseAnalytics = Firebase.analytics
}