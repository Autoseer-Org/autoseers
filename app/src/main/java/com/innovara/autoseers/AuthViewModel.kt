package com.innovara.autoseers

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private var currentVerificationStatus: VerificationStatus = VerificationStatus.UNKNOWN


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
                        // User has been signed in. Take user to home}
                    }

                    task.isCanceled || task.exception != null -> {
                        // User encountered error
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
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(defaultTimeout, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
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
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("Verification process", "Verification code sent")
                super.onCodeSent(verificationId, token)
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