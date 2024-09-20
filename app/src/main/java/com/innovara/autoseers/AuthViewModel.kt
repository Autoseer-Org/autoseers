package com.innovara.autoseers

import android.content.Context
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

data class AuthInProgressModel(
    val shouldTransitionToCodeScreen: Boolean,
    val shouldTransitionToNameScreen: Boolean,
    val verificationId: String = "",
)

data class AuthAuthenticatedModel @Inject constructor(
    val userName: String = "",
    val context: Context
) {
    companion object {
        private const val KEY_ALIAS = "my_key_alias"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    }

    // Create or access a secret key in the Android Keystore
    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        return if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Generate a new key if it doesn't exist
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false)
                    .build()
            )
            keyGenerator.generateKey()
        } else {
            // Return the existing key
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        }
    }

    private fun encryptToken(token: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv // Initialization Vector
        val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        // You need to save the IV alongside the encrypted data for decryption
        return iv + encryptedToken
    }

    private fun decryptToken(encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Split the IV and the encrypted data
        val iv = encryptedData.copyOfRange(0, 12) // First 12 bytes are the IV
        val encryptedToken = encryptedData.copyOfRange(12, encryptedData.size)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        val decryptedToken = cipher.doFinal(encryptedToken)
        return String(decryptedToken, Charsets.UTF_8)
    }

    fun storeNewToken(tokenId: String) {
        val encryptedToken = encryptToken(tokenId)
        val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("encrypted_token", Base64.encodeToString(encryptedToken, Base64.DEFAULT))
        editor.apply()
    }

    fun getToken(): String {
        val encryptedTokenString = context
            .getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            .getString("encrypted_token", null)

        if (encryptedTokenString != null) {
            val encryptedToken = Base64.decode(encryptedTokenString, Base64.DEFAULT)
            val token = decryptToken(encryptedToken)
            println("Decrypted token: $token")
            return "Bearer $token"
        }
        return ""
    }
}

sealed class AuthState {
    data object Loading: AuthState()
    data object NotAuthenticated : AuthState()
    data class PhoneVerificationInProgress(
        val authInProgressModel: AuthInProgressModel,
        val loading: Boolean = false,
        val errorMessage: String? = null,
        val error: Boolean,
    ) : AuthState()

    data class UserAuthenticated(
        val authAuthenticatedModel: AuthAuthenticatedModel,
        val shouldSkipNameStep: Boolean = false,
    ) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
) : ViewModel() {

    private var currentVerificationStatus: VerificationStatus = VerificationStatus.UNKNOWN

    private val _authState: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState
    val context: Context? = null


    /*
     * Call this when user has entered the verification token
     *  and we have a [PhoneAuthCredential] object
     */
    private fun signInWithPhoneAuthCredential(
        auth: FirebaseAuth,
        credential: PhoneAuthCredential,
        activity: MainActivity
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                when {
                    task.isSuccessful -> {
                        // User has been signed in. Take user to home
                        val user = task.result.user
                        user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                            _authState.update {
                                val authenticatedModel = AuthAuthenticatedModel(context = activity)
                                authenticatedModel.storeNewToken(tokenId = tokenTask.result.token ?: "")
                                AuthState.UserAuthenticated(
                                    authAuthenticatedModel = authenticatedModel
                                )
                            }
                        }
                    }

                    task.isCanceled || task.exception != null -> {
                        // User encountered error
                        _authState.update {
                            when (it) {
                                is AuthState.PhoneVerificationInProgress -> it.copy(
                                    authInProgressModel = it.authInProgressModel.copy(
                                        shouldTransitionToCodeScreen = false
                                    ),
                                    error = true,
                                    errorMessage = "Incorrect code. Please try again.",
                                )

                                else -> it
                            }
                        }
                    }
                }
            }
    }

    /*
     * The verifyPhoneNumber method is reentrant:
     * if you call it multiple times, such as in an activity's onStart method,
     * the verifyPhoneNumber method will not send a second SMS unless the original request has timed out.
     */
    fun createPhoneAuthOptions(
        auth: FirebaseAuth,
        phoneNumber: String,
        activity: MainActivity,
        defaultTimeout: Long = 60L
    ) {
        _authState.update {
            AuthState.PhoneVerificationInProgress(
                authInProgressModel = AuthInProgressModel(false, false),
                error = false
            )
        }
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(defaultTimeout, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setTimeout(15L, TimeUnit.SECONDS)
            .setCallbacks(
                verificationStepsCallback(
                    auth,
                    activity
                )
            ) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        currentVerificationStatus = VerificationStatus.VERIFYING
    }

    // Handle phone verification request
    private fun verificationStepsCallback(
        auth: FirebaseAuth,
        activity: MainActivity,
    ): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(authCredentials: PhoneAuthCredential) {
                Log.d("Verification process", "Verification completed")
                signInWithPhoneAuthCredential(auth = auth, authCredentials, activity)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                Log.d("Verification process", "Verification failed")
                when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        _authState.update {
                            when (it) {
                                is AuthState.PhoneVerificationInProgress -> it.copy(
                                    error = true,
                                    errorMessage = "fail to identify and/or authenticate the user subject of that operation."
                                )

                                else -> it
                            }
                        }
                    }

                    is FirebaseTooManyRequestsException -> {
                        _authState.update {
                            when (it) {
                                is AuthState.PhoneVerificationInProgress -> it.copy(
                                    error = true,
                                    errorMessage = "Firebase service has been blocked due to having received too many consecutive requests from the same device. Retry the request later to resolve."
                                )

                                else -> it
                            }
                        }
                    }

                    else -> {
                        _authState.update {
                            when (it) {
                                is AuthState.PhoneVerificationInProgress -> it.copy(
                                    error = true,
                                    errorMessage = "fail to identify and/or authenticate the user subject of that operation."
                                )

                                else -> it
                            }
                        }
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("Verification process", "Verification code sent")
                super.onCodeSent(verificationId, token)
                _authState.update {
                    AuthState.PhoneVerificationInProgress(
                        authInProgressModel = AuthInProgressModel(
                            shouldTransitionToCodeScreen = true,
                            shouldTransitionToNameScreen = false,
                            verificationId = verificationId,
                        ),
                        error = false,
                    )
                }
            }
        }
    }

    /*
     * Store the verification process last step from the user.
     * This will be helpful when trying to resume the user's verification step
     * in case when they dismiss the app
     */
    fun storeCurrentVerificationStep(outState: Bundle) {
        outState.putString(CURRENT_VERIFICATION_KEY, currentVerificationStatus.name)
    }

    fun manualCodeEntered(code: String, auth: FirebaseAuth, activity: MainActivity) {
        _authState.update {
            when (it) {
                is AuthState.PhoneVerificationInProgress -> {
                    it.copy(error = false, errorMessage = null)
                }

                else -> it
            }
        }
        val currentState = authState.value
        if (currentState is AuthState.PhoneVerificationInProgress) {
            val credentials = PhoneAuthProvider.getCredential(
                currentState.authInProgressModel.verificationId,
                code
            )
            signInWithPhoneAuthCredential(auth, credentials, activity)
        }
    }

    fun resetAuthState(authState: AuthState? = null) =
        _authState.update { authState ?: AuthState.NotAuthenticated }

    companion object {
        private const val CURRENT_VERIFICATION_KEY = "CURRENT_VERIFICATION_KEY"
    }
}

enum class VerificationStatus {
    NOT_STARTED,
    VERIFYING,
    DONE,
    FAILED,
    UNKNOWN,
}