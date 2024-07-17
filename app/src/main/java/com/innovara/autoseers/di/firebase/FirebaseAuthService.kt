package com.innovara.autoseers.di.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Inject

interface FirebaseAuthService {
    fun auth(): FirebaseAuth
}

class FirebaseAuthServiceImpl @Inject constructor() : FirebaseAuthService {
    override fun auth(): FirebaseAuth {
        return Firebase.auth
    }
}