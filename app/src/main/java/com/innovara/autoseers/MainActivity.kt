package com.innovara.autoseers

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.AutoSeersTheme
import com.innovara.autoseers.di.firebase.FirebaseAuthService
import com.innovara.autoseers.navigation.NavigationAppManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuthService: FirebaseAuthService

    private val authViewModel: AuthViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val auth = firebaseAuthService.auth()
        when (auth.currentUser) {
            null -> {
                // User is not sign in. Take them to the onboarding page
            }

            else -> {
                // User is signed in. Take them to the home page
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoSeersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavigationAppManager(
                        onPhoneNumberEntered = { phoneNumber ->
                            authViewModel.createPhoneAuthOptions(
                                firebaseAuthService.auth(),
                                phoneNumber,
                                this
                            )
                        }
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        authViewModel.storeCurrentVerificationStep(outState)
        super.onSaveInstanceState(outState)
    }
}