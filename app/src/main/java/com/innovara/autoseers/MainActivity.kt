package com.innovara.autoseers

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.compose.AutoSeersTheme
import com.innovara.autoseers.di.firebase.FirebaseService
import com.innovara.autoseers.navigation.NavigationAppManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseService: FirebaseService

    private val authViewModel: AuthViewModel by viewModels()

    private var isReady = false
    override fun onStart() {
        super.onStart()
        val auth = firebaseService.auth
        when (auth.currentUser) {
            null -> {
                // User is not sign in. Take them to the onboarding page
                // Reset the auth state to Not authenticated and show first frame
                authViewModel.resetAuthState(
                    authState = AuthState.NotAuthenticated
                )
                isReady = true
            }

            else -> {
                // User is signed in. Take them to the home page
                auth.currentUser?.getIdToken(false)
                    ?.addOnFailureListener {
                        isReady = true
                        authViewModel.resetAuthState(
                            authState = AuthState.NotAuthenticated
                        )
                        Log.e("TASK", it.localizedMessage ?: "")
                    }
                    ?.addOnCompleteListener {
                        if (it.exception != null) {
                            return@addOnCompleteListener
                        } else {
                            val authAuthenticatedModel = AuthAuthenticatedModel(context = this)
                            authAuthenticatedModel.storeNewToken(tokenId = it.result.token ?: "")
                            authViewModel.resetAuthState(
                                authState = AuthState.UserAuthenticated(
                                    authAuthenticatedModel = authAuthenticatedModel,
                                    shouldSkipNameStep = true
                                )
                            )
                            isReady = true
                        }
                    }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )
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
                        onLogoutPressed = {
                            firebaseService.auth.signOut()
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

    private fun setupAuthViewModel() {
        val authViewModel: AuthViewModel = AuthViewModel()
    }
}