package com.innovara.autoseers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.AutoSeersTheme
import com.innovara.autoseers.di.firebase.FirebaseService
import com.innovara.autoseers.navigation.NavigationAppManager
import com.innovara.autoseers.onboarding.logic.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseService: FirebaseService

    private val authViewModel: AuthViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        val auth = firebaseService.auth
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
                    val authState by authViewModel.authState.collectAsState()
                    NavigationAppManager(
                        authState = authState,
                        onPhoneNumberEntered = { phoneNumber ->
                            authViewModel.createPhoneAuthOptions(
                                auth = firebaseService.auth,
                                phoneNumber = phoneNumber,
                                activity = this
                            )
                        },
                        onCodeEntered = {
                            authViewModel.manualCodeEntered(
                                code = it,
                                auth = firebaseService.auth,
                                activity = this
                            )
                        },
                        resetAuthState = authViewModel::resetAuthState,
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