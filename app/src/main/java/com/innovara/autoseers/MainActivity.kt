package com.innovara.autoseers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.compose.AutoSeersTheme
import com.innovara.autoseers.di.firebase.FirebaseService
import com.innovara.autoseers.navigation.NavigationAppManager
import com.innovara.autoseers.settings.ui.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
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
        firebaseService.remoteConfig().fetch()
        firebaseService.remoteConfig().fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.e("Remote config", "Successfully fetch features flags: ${task.result}")
            }
        }
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
            val isDarkTheme by applicationContext.dataStore.data
                .map { preferences ->
                    preferences[SettingsViewModel.PreferencesKeys.IS_DARK_THEME]
                        ?: false // Default to light theme
                }.collectAsState(initial = false)

            AutoSeersTheme(darkTheme = isDarkTheme) {
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
                        },
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